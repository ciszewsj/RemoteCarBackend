package ee.eee.testwebsock.websockets.websocket.car;

import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Slf4j
public class CarClient {
	private URI uri;
	private WebSocketClient client;
	private WebSocketHandler webSocketHandler;

	private WebSocketSession socketSession;
	private UserControllerUseCase userController;

	private ControlMessage lastControlMessage;

	public CarClient(UserControllerUseCase userController) {
		this.userController = userController;
		try {
			this.uri = new URI("ws://localhost:8000/");
			this.client = new StandardWebSocketClient();

			this.webSocketHandler = new WebSocketHandler() {

				@Override
				public void afterConnectionEstablished(WebSocketSession session) throws Exception {
					socketSession = session;
					session.sendMessage(new TextMessage("!321414"));
				}

				@Override
				public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
//					log.error(message.toString());
					userController.sendFrameToUsers(message.toString().getBytes(StandardCharsets.UTF_8));
				}

				@Override
				public void handleTransportError(WebSocketSession session, Throwable exception) {

				}

				@Override
				public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
					socketSession = null;
				}

				@Override
				public boolean supportsPartialMessages() {
					return false;
				}
			};

			client.execute(this.webSocketHandler, null, uri).get();


		} catch (URISyntaxException | ExecutionException | InterruptedException e) {
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

}
