package ee.eee.testwebsock.websockets.websocket.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.car.CarControlMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Slf4j
public class CarClient {
	private URI uri;

	private final int tickRate = 20;
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

	public CarClient(String uri, Integer fps, UserControllerUseCase userController) {
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.fps = fps;
		this.userController = userController;
		this.client = new StandardWebSocketClient();


		this.webSocketHandler = new WebSocketHandler() {
			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				socketSession = session;
				controlFuture = controlFunction();
			}

			@Override
			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
				log.debug("Handle message {}", message);
				userController.sendFrameToUsers(message.toString().getBytes(StandardCharsets.UTF_8));
			}

			@Override
			public void handleTransportError(WebSocketSession session, Throwable exception) {

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

	public void sendCommand(String command) throws IOException {
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
				if (currentControlMessage == null) {
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

	public boolean isConnected() {
		return socketSession != null && socketSession.isOpen();
	}
}
