package ee.eee.testwebsock.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.websockets.data.FrameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Slf4j

public class WebSocketCarHandler implements WebSocketHandler {
	private static final Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<>());
	ObjectMapper objectMapper = new ObjectMapper();
	private byte[] kolo;
	private byte[] triangle;
	private byte[] triangle2;


	public WebSocketCarHandler() {
		kolo = null;
		try {
			kolo = new ClassPathResource("kolo.png").getInputStream().readAllBytes();
			triangle = new ClassPathResource("triangle.jpg").getInputStream().readAllBytes();
			triangle2 = new ClassPathResource("square.jpg").getInputStream().readAllBytes();
		} catch (
				Exception e) {
			log.error(e.getMessage());
		}
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
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		log.error("Message from " + session.getId() + ": " + message);
//		sendImageToAllUsers(null);
		synchronized (clients) {
			for (WebSocketSession client : clients) {
				if (!client.equals(session)) {
					try {
						client.sendMessage(message);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						client.sendMessage(message);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
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

	public void sendImageToAllUsers(Byte[] images) {
		try {
			byte[] resource = switch ((int) (Math.random() * (3))) {
				case 0 -> kolo;
				case 1 -> triangle;
				default -> triangle2;
			};
//			InputStream in = resource.getInputStream();

			FrameMessage frameMessage = new FrameMessage();
			frameMessage.setImage(resource);
			WebSocketMessage<String> msg = new TextMessage(objectMapper.writeValueAsString(frameMessage));
			clients.forEach(client -> {
				try {
					client.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
