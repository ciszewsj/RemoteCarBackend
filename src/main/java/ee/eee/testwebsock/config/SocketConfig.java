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

import java.util.Arrays;
import java.util.Map;

import static ee.eee.testwebsock.utils.ImageObject.imageSizes;

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
				try {
					String imgSize = request.getHeaders().getOrEmpty("resolution").get(0);
					if (Arrays.stream(imageSizes).map(Enum::toString).anyMatch(str -> str.equals(imgSize))) {
						attributes.put("resolution", imgSize);
					} else {
						log.info("Resolution is not set");
					}
				} catch (Exception e) {
					log.error("Could not read resolution");

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
