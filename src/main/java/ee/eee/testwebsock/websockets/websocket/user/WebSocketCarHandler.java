package ee.eee.testwebsock.websockets.websocket.user;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.user.UserControlMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class WebSocketCarHandler implements WebSocketHandler {
	ObjectMapper objectMapper = new ObjectMapper();
	private final UserControllerUseCase userController;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.error(session.getAttributes().get("auctionId") + "HERE >");
		userController.addSession(session);
		log.error(session.getId() + " has opened a connection " + session.getUri());

		try {
			WebSocketMessage<String> msg = new TextMessage("Connection Established");
			session.sendMessage(msg);
		} catch (IOException ex) {
			log.error("Not Working ");
			ex.printStackTrace();
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws JsonProcessingException {
		log.error("Message from " + session.getId() + ": " + message.getPayload());

		UserControlMessage<?> userControlMessage = objectMapper.readValue(message.getPayload().toString(), UserControlMessage.class);

		log.error("??? TYPE IS {}", userControlMessage.getType());

		if (userControlMessage.getType().equals(UserControlMessage.UserControlMessageType.CONTROL_MESSAGE)) {
			ControlMessage controlMessage = objectMapper.readValue(userControlMessage.getData().toString(), ControlMessage.class);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		userController.closeSession(session);

		log.error("Session " + session.getId() + " has ended");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}


}
