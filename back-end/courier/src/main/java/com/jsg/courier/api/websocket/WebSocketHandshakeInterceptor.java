package com.jsg.courier.api.websocket;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.jsg.courier.utilities.JWTHandler;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		Cookie[] cookies = req.getCookies();
		if(cookies == null) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			return false;
		}
		String idString = request.getHeaders().getFirst("sec-websocket-protocol");
		if(idString == null || idString.contentEquals("")) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			return false;
		}
		String token = cookies[0].getValue();
		if(!JWTHandler.verifyToken(token)) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			return false;
		}
		long id = Long.parseLong(idString);
		if(JWTHandler.getIdFromToken(token) != id) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			return false;
		}
		response.setStatusCode(HttpStatus.OK);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
	}

}
