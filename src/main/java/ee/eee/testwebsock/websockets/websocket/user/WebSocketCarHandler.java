package ee.eee.testwebsock.websockets.websocket.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.user.UserControlMessage;
import ee.eee.testwebsock.websockets.data.user.UserInfoMessage;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class WebSocketCarHandler implements WebSocketHandler {
	ObjectMapper objectMapper = new ObjectMapper();
	private final UserControllerUseCase userController;
	private final CarControllerUseCase carController;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info(session.getId() + " has opened a connection " + session.getUri() + " to car " + session.getAttributes().get("carId"));

		if (session.getAttributes().get("carId") == null
				|| !carController.isCarExists((Long) session.getAttributes().get("carId"))
				|| !carController.isCarRunning((Long) session.getAttributes().get("carId"))
		) {
			UserControlMessage<UserInfoMessage> controlMessage = new UserControlMessage<>(
					UserControlMessage.UserControlMessageType.INFO_MESSAGE,
					UserInfoMessage.builder()
							.msg(UserInfoMessage.UserInfoType.CAR_NOT_EXISTS)
							.websocketId(session.getId())
							.build()
			);
			try {
				WebSocketMessage<String> msg = new TextMessage(objectMapper.writeValueAsString(controlMessage));
				session.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			userController.addSession(session);
			UserControlMessage<UserInfoMessage> controlMessage = new UserControlMessage<>(
					UserControlMessage.UserControlMessageType.INFO_MESSAGE,
					UserInfoMessage.builder()
							.msg(UserInfoMessage.UserInfoType.CONNECTED_SUCCESSFULLY)
							.websocketId(session.getId())
							.build()
			);
			try {
				WebSocketMessage<String> msg = new TextMessage(objectMapper.writeValueAsString(controlMessage));
				session.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		try {

			UserControlMessage<?> userControlMessage = objectMapper.readValue(message.getPayload().toString(), UserControlMessage.class);
			if (userControlMessage.getType().equals(UserControlMessage.UserControlMessageType.CONTROL_MESSAGE)) {
				ControlMessage controlMessage = objectMapper.convertValue(userControlMessage.getData(), ControlMessage.class);
				log.info("SPEED : {} - {}", controlMessage.getHorizontalSpeed(), controlMessage.getVerticalSpeed());
				log.info(session.getId());
				carController.controlCar((long) session.getAttributes().get("carId"), controlMessage, session.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error("session handleTransportError", exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		userController.closeSession(session);
		log.info("Session " + session.getId() + " has ended");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
