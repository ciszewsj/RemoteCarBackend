package ee.eee.testwebsock.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

@Slf4j
@Component
public class CarClient {
	public CarClient() {
		WebSocketClient client = new StandardWebSocketClient();

		WebSocketStompClient s = new WebSocketStompClient(client);
		s.setMessageConverter(new MappingJackson2MessageConverter());


		StompSessionHandler sessionHandler = new StompSessionHandler() {
			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				log.error("Connected");
			}

			@Override
			public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

			}

			@Override
			public void handleTransportError(StompSession session, Throwable exception) {

			}

			@Override
			public Type getPayloadType(StompHeaders headers) {
				return null;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {

			}
		};

		s.connectAsync("ws://localhost:8000/", sessionHandler);
		s.start();
	}


}
