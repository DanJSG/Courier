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

import com.jsg.courier.datatypes.AuthToken;

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
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		Cookie cookie = WebUtils.getCookie(req, ACCESS_TOKEN_NAME);
		if(cookie == null) {
			return false;
		}
		AuthToken token = new AuthToken(cookie.getValue());
		if(!token.verify(ACCESS_TOKEN_SECRET)) {
			return false;
		}
		response.setStatusCode(HttpStatus.OK);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {}

}
