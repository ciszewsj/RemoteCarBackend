package ee.eee.testwebsock.websockets.websocket.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.websockets.data.car.CarFrameMessage;
import ee.eee.testwebsock.websockets.data.user.UserControlMessage;
import ee.eee.testwebsock.websockets.data.user.UserFrameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class UserController implements UserControllerUseCase {
	private static final Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());
	ObjectMapper objectMapper = new ObjectMapper();


	@Override
	public void addSession(WebSocketSession session) {
		clients.add(session);
	}

	@Override
	public void closeSession(WebSocketSession session) {
		clients.remove(session);
	}

	@Override
	public void sendFrameToUsers(Long carId, byte[] frame) {
		CarFrameMessage frameMessage = new CarFrameMessage();
		frameMessage.setImage(frame);
		clients.forEach(client -> {
			try {
				if ((long) client.getAttributes().get("carId") == carId) {
					client.sendMessage(new TextMessage(
							objectMapper.writeValueAsString(
									new UserControlMessage<>(
											UserControlMessage.UserControlMessageType.DISPLAY_MESSAGE,
											UserFrameMessage.builder()
													.frame(frame)
													.build()
									)
							)
					));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
