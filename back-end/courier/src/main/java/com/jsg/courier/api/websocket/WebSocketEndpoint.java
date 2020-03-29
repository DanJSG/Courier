package com.jsg.courier.api.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketEndpoint implements WebSocketConfigurer {
	
	@Autowired
	SocketHandler socketHandler;
	
	@Autowired
	WebSocketHandshakeInterceptor handshakeInterceptor;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(socketHandler, "/api/ws")
		.setAllowedOrigins("http://local.courier.net:3000")
		.addInterceptors(handshakeInterceptor);	
	}
}
