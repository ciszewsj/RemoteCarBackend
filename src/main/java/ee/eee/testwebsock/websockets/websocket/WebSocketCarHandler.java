package ee.eee.testwebsock.websockets.websocket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.car.FrameMessage;
import ee.eee.testwebsock.websockets.data.user.UserControlMessage;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class WebSocketCarHandler implements WebSocketHandler, UserControllerUseCase {
	private static final Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());
	ObjectMapper objectMapper = new ObjectMapper();


	public WebSocketCarHandler() {
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.error(session.getAttributes().get("auctionId") + "HERE >");
		clients.add(session);
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
//			carControllerUseCase.controlCar(controlMessage);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		clients.remove(session);
		log.error("Session " + session.getId() + " has ended");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	@Override
	public void sendFrameToUsers(byte[] frame) {
		FrameMessage frameMessage = new FrameMessage();
		frameMessage.setImage(frame);
		clients.forEach(client -> {
			try {
				client.sendMessage(new TextMessage(objectMapper.writeValueAsString(frameMessage)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
