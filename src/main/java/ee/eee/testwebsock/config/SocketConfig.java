package ee.eee.testwebsock.config;

import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import ee.eee.testwebsock.websockets.websocket.user.WebSocketCarHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@EnableWebSocket
@Configuration
@RequiredArgsConstructor
public class SocketConfig implements WebSocketConfigurer {

	private final UserControllerUseCase userControllerUseCase;
	private final CarControllerUseCase carControllerUseCase;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getWsEndpoint(), "/cars/*").setAllowedOriginPatterns("*").addInterceptors(auctionInterceptor());
	}

	private HandshakeInterceptor auctionInterceptor() {
		return new HandshakeInterceptor() {
			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			                               WebSocketHandler wsHandler, Map<String, Object> attributes) {
				String path = request.getURI().getPath();
				try {
					Long carId = Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
					attributes.put("carId", carId);
				} catch (Exception e) {
					log.error("Could not read carId", e);
				}
				return true;
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			                           WebSocketHandler wsHandler, Exception exception) {
			}
		};
	}

	@Bean
	public WebSocketHandler getWsEndpoint() {
		return new WebSocketCarHandler(userControllerUseCase, carControllerUseCase);
	}
}
