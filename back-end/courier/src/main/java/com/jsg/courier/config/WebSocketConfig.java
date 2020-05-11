package com.jsg.courier.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.jsg.courier.api.websocket.SocketHandler;
import com.jsg.courier.api.websocket.WebSocketHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	SocketHandler socketHandler;
	
	@Autowired
	WebSocketHandshakeInterceptor handshakeInterceptor;
	
	private static String[] origins;
	
	@Autowired
	public WebSocketConfig(@Value("${cors.origins}") String[] origins) {
		WebSocketConfig.origins = origins;
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(socketHandler, "/api/v1/ws")
		.setAllowedOrigins(origins)
		.addInterceptors(handshakeInterceptor);	
	}
}
