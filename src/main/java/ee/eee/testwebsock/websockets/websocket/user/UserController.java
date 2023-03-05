package ee.eee.testwebsock.websockets.websocket.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.utils.ImageObject;
import ee.eee.testwebsock.utils.ImageResizer;
import ee.eee.testwebsock.websockets.data.user.UserControlMessage;
import ee.eee.testwebsock.websockets.data.user.UserFrameMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static ee.eee.testwebsock.utils.ImageObject.defaultImageSize;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserController implements UserControllerUseCase {
	private static final Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());

	private final ImageResizer imageResizer;
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

		ImageObject imageObject = new ImageObject(frame, imageResizer);

		Arrays.stream(ImageObject.imageSizes)
				.forEach(size -> {
					TextMessage message;
					try {
						message = new TextMessage(
								objectMapper.writeValueAsString(
										new UserControlMessage<>(
												UserControlMessage.UserControlMessageType.DISPLAY_MESSAGE,
												UserFrameMessage.builder()
														.frame(imageObject.getImageBySize(size))
														.build()
										)
								)
						);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
						return;
					}

					clients.forEach(client -> {
						try {
							if ((long) client.getAttributes().get("carId") == carId) {
								if ((client.getAttributes().get("resolution") == null && size == defaultImageSize)
										|| size.toString().equals(client.getAttributes().get("resolution"))) {
									client.sendMessage(message);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				});
	}
}
