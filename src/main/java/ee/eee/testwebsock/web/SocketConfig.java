package ee.eee.testwebsock.web;

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
@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getWsEndpoint(), "/echo/*").setAllowedOriginPatterns("*").addInterceptors(auctionInterceptor());
	}

	@Bean
	public HandshakeInterceptor auctionInterceptor() {
		return new HandshakeInterceptor() {


			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			                               WebSocketHandler wsHandler, Map<String, Object> attributes) {

				String path = request.getURI().getPath();
				String auctionId = path.substring(path.lastIndexOf('/') + 1);

				attributes.put("auctionId", auctionId);

				log.info(auctionId);
				return true;
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			                           WebSocketHandler wsHandler, Exception exception) {
			}
		};
	}

	@Bean
	public WebSocketCarHandler getWsEndpoint() {
		return new WebSocketCarHandler();
	}
}
