package ee.eee.testwebsock.websockets.websocket.car;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Date;
import java.util.concurrent.*;

@Slf4j
public class CarClient {
	private URI uri;

	private final Long id;
	private final int tickRate = 20;
	private final long maxMessageDelay = 500;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final CarControlMessage<ControlMessage> carControlMessage = new CarControlMessage<>(CarControlMessage.CarControlMessageType.CONTROL_MESSAGE, null);

	private final WebSocketClient client;
	private final WebSocketHandler webSocketHandler;

	private WebSocketSession socketSession;
	private final UserControllerUseCase userController;

	private ScheduledFuture<?> controlFuture;

	private Integer fps;

	private ControlMessage currentControlMessage;

	private Long currentMessageTime;

	public CarClient(Long id, String uri, Integer fps, UserControllerUseCase userController) {
		this.id = id;
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.userController = userController;
		this.client = new StandardWebSocketClient();
		this.fps = fps;

		this.webSocketHandler = webSocketHandler();

	}

	private WebSocketHandler webSocketHandler() {
		return new WebSocketHandler() {
			@Override
			public void afterConnectionEstablished(WebSocketSession session) {
				socketSession = session;
				CarConfigMessage configMessage = CarConfigMessage.builder().fps(fps).build();
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
						CarFrameMessage frameMessage = objectMapper.readValue(carControlMessage.getData().toString(), CarFrameMessage.class);
						userController.sendFrameToUsers(id, frameMessage.getImage());
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
				socketSession = null;
				controlFuture.cancel(true);
			}

			@Override
			public boolean supportsPartialMessages() {
				return false;
			}
		};
	}

	public void connect() {
		try {
			log.error("Client : ? {}", client);
			client.execute(this.webSocketHandler, null, uri).get();
		} catch (ExecutionException | InterruptedException e) {
			log.error("Could not start car", e);
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_START_ERROR);
		}
	}

	public void disconnect() {
		try {
			socketSession.close();
			controlFuture.cancel(true);
		} catch (IOException e) {
			log.error("Could not stop car", e);
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_STOP_ERROR);
		}
	}

	public void controlCar(ControlMessage controlMessage) {
		currentControlMessage = controlMessage;
		currentMessageTime = new Date().getTime();
	}

	private void sendCommand(String command) throws IOException {
		if (socketSession != null) {
			socketSession.sendMessage(new TextMessage(command));
		} else {
			throw new NullPointerException("Connection not ready yet");
		}
	}

	private ScheduledFuture<?> controlFunction() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		return executor.scheduleAtFixedRate(() -> {
			try {
				if (new Date().getTime() > currentMessageTime + maxMessageDelay) {
					carControlMessage.setData(new ControlMessage());
				} else {
					carControlMessage.setData(currentControlMessage);
				}
				this.sendCommand(objectMapper.writeValueAsString(carControlMessage));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
	}

	public void configure(String uri, Integer fps) {
		if (isConnected()) {
			throw new WebControllerException(WebControllerException.ExceptionStatus.COULD_NOT_CONFIGURE_CAR_WHILE_RUNNING);
		}
		this.uri = null;
		try {
			this.uri = new URI(uri);
			this.fps = fps;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return socketSession != null && socketSession.isOpen();
	}
}
