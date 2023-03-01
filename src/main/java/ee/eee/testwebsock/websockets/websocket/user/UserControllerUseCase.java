package ee.eee.testwebsock.websockets.websocket.user;

import org.springframework.web.socket.WebSocketSession;

public interface UserControllerUseCase {
	void addSession(WebSocketSession session);

	void closeSession(WebSocketSession session);

	void sendFrameToUsers(Long carId, byte[] frame);
}
