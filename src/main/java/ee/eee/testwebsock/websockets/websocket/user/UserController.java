package ee.eee.testwebsock.websockets.websocket.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.utils.ImageObject;
import ee.eee.testwebsock.utils.ImageResizer;
import ee.eee.testwebsock.websockets.data.user.UserControlMessage;
import ee.eee.testwebsock.websockets.data.user.UserFrameMessage;
import ee.eee.testwebsock.websockets.data.user.UserInfoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

import static ee.eee.testwebsock.utils.ImageObject.defaultImageSize;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserController implements UserControllerUseCase {
	private static final Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());

	ObjectMapper objectMapper = new ObjectMapper();
	private final ImageResizer resizer;

	@Override
	public void addSession(WebSocketSession session) {
		clients.add(session);
	}

	@Override
	public void closeSession(WebSocketSession session) {
		clients.remove(session);
	}

	@Override
	public void sendFrameToUsers(Long carId, String userRentId, String sessionId, Long leftTime, byte[] frame) {
		try {
			Map<ImageObject.ImageSize, byte[]> frameMap = resizer.resizeImageOnMap(frame);

			Arrays.stream(ImageObject.imageSizes)
					.forEach(size -> {
						TextMessage message;
						try {
							message = new TextMessage(
									objectMapper.writeValueAsString(
											new UserControlMessage<>(
													UserControlMessage.UserControlMessageType.DISPLAY_MESSAGE,
													UserFrameMessage.builder()
															.frame(frameMap.get(size))
															.userRentId(userRentId)
															.timeToEnd(leftTime)
															.sessionSteeringId(sessionId)
															.build()
											)
									)
							);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
							return;
						}
						try {
							clients.forEach(client -> {
								try {
									if ((long) client.getAttributes().get("carId") == carId) {
										if ((client.getAttributes().get("resolution") == null && size == defaultImageSize)
												|| size.equals(client.getAttributes().get("resolution"))) {
											client.sendMessage(message);
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void closeCar(Long carId) {
		UserControlMessage<UserInfoMessage> controlMessage = new UserControlMessage<>(
				UserControlMessage.UserControlMessageType.INFO_MESSAGE,
				UserInfoMessage.builder()
						.msg(UserInfoMessage.UserInfoType.CAR_DISCONNECTED)
						.build());
		TextMessage message;
		try {
			message = new TextMessage(objectMapper.writeValueAsString(controlMessage));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}

		try {
			clients.forEach(client -> {
				try {
					if ((long) client.getAttributes().get("carId") == carId) {
						client.sendMessage(message);
						client.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
