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
		String protocolHeader = request.getHeaders().getFirst("sec-websocket-protocol");
		if(cookies == null || protocolHeader == null || protocolHeader.contentEquals("")) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		String[] protocolHeaders = protocolHeader.split(",");
		if(protocolHeaders.length != 2) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		String idString = protocolHeaders[0];
		if(idString == null || idString.contentEquals("")) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		String token = cookies[0].getValue();
		String headerToken = protocolHeaders[1].trim();
		long id = Long.parseLong(idString);
		if(!JWTHandler.tokenIsValid(token, id) || !JWTHandler.tokenIsValid(headerToken, id)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
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
