package ee.eee.testwebsock.websockets.websocket.user;

import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

public interface UserControllerUseCase {
	void addSession(WebSocketSession session);

	void closeSession(WebSocketSession session);

	void sendFrameToUsers(Long carId, String userRentId, String sessionId, Long leftTime, byte[] frame);
}
