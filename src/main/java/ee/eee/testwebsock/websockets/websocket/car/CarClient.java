package ee.eee.testwebsock.websockets.websocket.car;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.car.CarConfigMessage;
import ee.eee.testwebsock.websockets.data.car.CarControlMessage;
import ee.eee.testwebsock.websockets.data.car.CarFrameMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.*;

@Slf4j
public class CarClient {
	private URI uri;
	private final Long id;

	private final int tickRate;
	private final long maxMessageDelay;
	private Long currentMessageTime;
	private ControlMessage currentControlMessage;

	private final CarImplService carImplService;

	private final ObjectMapper objectMapper;
	private final CarControlMessage<ControlMessage> carControlMessage = new CarControlMessage<>(CarControlMessage.CarControlMessageType.CONTROL_MESSAGE, null);

	private final WebSocketClient client;
	private final WebSocketHandler webSocketHandler;

	private WebSocketSession socketSession;
	private final UserControllerUseCase userController;
	private ScheduledFuture<?> controlFuture;

	private String userId;
	private String websocketId;
	private Long endTime;

	private Boolean isStopped = true;

	private final Long timeForRent;

	public CarClient(Long id,
	                 String uri,
	                 UserControllerUseCase userController,
	                 CarImplService carImplService,
	                 Long timeForRent,
	                 Integer tickRate,
	                 Long maxMessageDelay) {
		this.id = id;
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.userController = userController;
		this.client = new StandardWebSocketClient();

		this.webSocketHandler = webSocketHandler(this.id);

		this.currentMessageTime = 0L;
		this.carImplService = carImplService;
		this.objectMapper = new ObjectMapper();
		this.timeForRent = timeForRent;
		this.tickRate = tickRate;
		this.maxMessageDelay = maxMessageDelay;
	}

	private WebSocketHandler webSocketHandler(Long id) {
		return new WebSocketHandler() {
			@Override
			public void afterConnectionEstablished(WebSocketSession session) {
				socketSession = session;

				socketSession.setTextMessageSizeLimit(10 * 1024 * 1024);

				CarConfigMessage configMessage = CarConfigMessage.builder().build();
				try {
					session.sendMessage(
							new TextMessage(
									objectMapper.writeValueAsString(
											new CarControlMessage<>(
													CarControlMessage.CarControlMessageType.CONFIG_MESSAGE,
													configMessage)
									)
							)
					);
				} catch (IOException e) {
					log.error("Could not send config message", e);
				}
				controlFuture = controlFunction();
			}

			@Override
			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
				try {
					CarControlMessage<?> carControlMessage = objectMapper.readValue(message.getPayload().toString(), CarControlMessage.class);
					if (carControlMessage.getType().equals(CarControlMessage.CarControlMessageType.DISPLAY_MESSAGE)) {
						CarFrameMessage frameMessage = objectMapper.convertValue(carControlMessage.getData(), CarFrameMessage.class);
						userController.sendFrameToUsers(id, userId, websocketId, leftControlTime(), Base64.getDecoder().decode(frameMessage.getImage()));
					} else if (carControlMessage.getType().equals(CarControlMessage.CarControlMessageType.INFO_MESSAGE)) {
						log.info("Received info from car");
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void handleTransportError(WebSocketSession session, Throwable exception) {
				log.error("Car handleTransportError", exception);
			}

			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
				log.warn("Car websocket session is closed");
				userController.closeCar(id);
			}

			@Override
			public boolean supportsPartialMessages() {
				return false;
			}
		};
	}

	public void connect() {
		try {
			client.execute(this.webSocketHandler, null, uri).get();

			carImplService.turnCarOn(id);
			carImplService.addCarStatus(id, CarStatusEntity.Status.CONNECTED);
			isStopped = false;
		} catch (ExecutionException | InterruptedException e) {
			log.error("Could not start car", e);

			carImplService.turnCarOff(id);
			carImplService.addCarStatus(id, CarStatusEntity.Status.CONNECTION_FAILURE);

			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_START_ERROR);
		}
	}

	public void disconnect() {
		try {
			carImplService.turnCarOff(id);
			carImplService.addCarStatus(id, CarStatusEntity.Status.DISCONNECTED);

			socketSession.close();
			controlFuture.cancel(true);
			isStopped = true;
		} catch (IOException e) {
			log.error("Could not stop car", e);
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_STOP_ERROR);
		}
	}

	public void controlCar(ControlMessage controlMessage, String websocketId) {
		if (!websocketId.equals(this.websocketId)) {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_NOT_STEERING_BY_YOU);
		}
		currentControlMessage = controlMessage;
		currentMessageTime = new Date().getTime();
	}

	private void sendCommand(String command) throws IOException {
		if (socketSession != null) {
			socketSession.sendMessage(new TextMessage(command));
		} else {
			log.error("Connection not ready yet");
			throw new NullPointerException("Connection not ready yet");
		}
	}

	private ScheduledFuture<?> controlFunction() {
		log.info("INITED CONTROL FUNCTION");
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		return executor.scheduleAtFixedRate(() -> {
			if (isStopped && endTime < new Date().getTime()) {
				disconnect();
				return;
			}
			try {
				if (new Date().getTime() > currentMessageTime + maxMessageDelay) {
					carControlMessage.setData(new ControlMessage());
				} else {
					carControlMessage.setData(currentControlMessage);
				}
				this.sendCommand(objectMapper.writeValueAsString(carControlMessage));
			} catch (IOException e) {
				log.error("Exception runtime", e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
	}

	public void configure(String uri) {
		if (isConnected()) {
			throw new WebControllerException(WebControllerException.ExceptionStatus.COULD_NOT_CONFIGURE_CAR_WHILE_RUNNING);
		}
		this.uri = null;
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		carImplService.addCarStatus(id, CarStatusEntity.Status.CONFIGURE);
	}

	public void rentCar(String userId) {
		Long time = new Date().getTime();
		log.info("Try rent car by {}, endtime {} current {}", userId, endTime, time);
		if (endTime == null || endTime < new Date().getTime()) {
			websocketId = null;
			this.userId = userId;
			endTime = new Date().getTime() + timeForRent;
		} else {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_RENTED);
		}
	}

	public void takeControlOverCar(String websocketId, String userId) {
		if (userId.equals(this.userId) && endTime > new Date().getTime()) {
			log.info("CONTROL CHANGED");
			this.websocketId = websocketId;
		} else {
			throw new WebControllerException(WebControllerException.ExceptionStatus.COULD_NOT_TAKE_CONTROL);
		}
	}

	public long leftControlTime() {
		if (endTime == null) {
			return 0;
		}
		long left = endTime - new Date().getTime();
		if (left < 0) {
			if (websocketId != null) {
				websocketId = null;
			}
			if (userId != null) {
				userId = null;
			}
		}
		return left < 0 ? 0 : left;
	}

	public boolean isConnected() {
		log.info("socket session {}", socketSession);
		return socketSession != null && socketSession.isOpen();
	}

	public void release() {
		isStopped = true;
	}
}
