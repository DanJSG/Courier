package com.jsg.hive.config;

import com.jsg.hive.messages.api.HandshakeInterceptor;
import com.jsg.hive.messages.api.SocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	private SocketController socketHandler;

	private HandshakeInterceptor handshakeInterceptor;
	
	private static String[] origins;
	
	@Autowired
	public WebSocketConfig(@Value("${CORS_ORIGINS}") String[] origins, SocketController socketController,
						   HandshakeInterceptor handshakeInterceptor) {
		WebSocketConfig.origins = origins;
		this.socketHandler = socketController;
		this.handshakeInterceptor = handshakeInterceptor;
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(socketHandler, "/api/v1/ws").setAllowedOrigins(origins)
				.addInterceptors(handshakeInterceptor);
	}
}
