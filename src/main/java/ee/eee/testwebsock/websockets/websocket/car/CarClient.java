package ee.eee.testwebsock.websockets.websocket.car;

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

	private final WebSocketClient client;
	private final WebSocketHandler webSocketHandler;

	private WebSocketSession socketSession;
	private final UserControllerUseCase userController;

	private ScheduledFuture<?> controlFuture;

	private Integer fps;

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

	public void connect() throws ExecutionException, InterruptedException {

		client.execute(this.webSocketHandler, null, uri).get();

	}

	public void disconnect() {
		try {
			socketSession.close();
		} catch (IOException e) {
			e.printStackTrace();
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
				this.sendCommand(new ClassPathResource("kolo.png").getInputStream().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
	}

}
