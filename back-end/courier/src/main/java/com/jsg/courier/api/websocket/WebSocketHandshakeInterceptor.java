package com.jsg.courier.api.websocket;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import com.jsg.courier.utilities.JWTHandler;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
	
	private static final String ACCESS_TOKEN_NAME = "acc.tok";
	private final String ACCESS_TOKEN_SECRET;
	
	@Autowired
	public WebSocketHandshakeInterceptor(@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret) {
		ACCESS_TOKEN_SECRET = accessTokenSecret;
	}
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		Cookie cookie = WebUtils.getCookie(req, ACCESS_TOKEN_NAME);
		String protocolHeader = request.getHeaders().getFirst("sec-websocket-protocol");
		System.out.println("Protocol header is:" + protocolHeader);
		if(cookie == null || protocolHeader == null || protocolHeader.contentEquals("")) {
			System.out.println("Cookies not present...");
			System.out.println(cookie);
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
		String token = cookie.getValue();
		System.out.println("Cookie token is: " + token);
		String headerToken = protocolHeaders[1].trim();
		if(!JWTHandler.tokenIsValid(token, ACCESS_TOKEN_SECRET) || 
				!JWTHandler.tokenIsValid(headerToken, ACCESS_TOKEN_SECRET)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		response.setStatusCode(HttpStatus.OK);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {}

}
