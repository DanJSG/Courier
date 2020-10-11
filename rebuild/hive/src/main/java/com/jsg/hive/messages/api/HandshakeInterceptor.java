package com.jsg.hive.messages.api;

import com.jsg.hive.auth.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class HandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {

	private static final String ACCESS_TOKEN_NAME = "acc.tok";
	private final String ACCESS_TOKEN_SECRET;

	@Autowired
	public HandshakeInterceptor(@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret) {
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
