package com.jsg.courier.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jsg.courier.datatypes.AuthToken;

@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
	
	private static final String ACCESS_TOKEN_NAME = "acc.tok";
	private final String ACCESS_TOKEN_SECRET;
	
	@Autowired
	private AuthorizationInterceptor(@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret) {
		ACCESS_TOKEN_SECRET = accessTokenSecret;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		System.out.println("IN REQUEST INTERCEPTOR");
		String method = request.getMethod();
		if(!method.contentEquals("POST") && !method.contentEquals("GET") && 
		   !method.contentEquals("PUT") && !method.contentEquals("DELETE")) {
			return true;
		}
		AuthToken authHeader = getAuthHeader(request);
		AuthToken authCookie = getAuthCookie(request);
		System.out.println("Auth header: " + authHeader);
		System.out.println("Auth cookie: " + authCookie);
		return checkTokens(authHeader, authCookie);
	}
	
	private boolean checkTokens(AuthToken tokenA, AuthToken tokenB) {
		if(tokenA == null || tokenB == null)
			return false;
		if(!tokenA.verify(ACCESS_TOKEN_SECRET) || !tokenB.verify(ACCESS_TOKEN_SECRET))
			return false;
		if(tokenA.getToken().contentEquals(tokenB.getToken()))
			return false;
		return true;
	}
	
	private static AuthToken getAuthHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("authorization");
		if(authHeader == null) {
			return null;
		}
		return new AuthToken(AuthHeaderHandler.getBearerToken(authHeader));
	}
	
	private static AuthToken getAuthCookie(HttpServletRequest request) {
		String authCookie = null;
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return null;
		}
		for(Cookie cookie : cookies) {
			if(!cookie.getName().contentEquals(ACCESS_TOKEN_NAME)) {
				continue;
			}
			authCookie = cookie.getValue();
		}
		return authCookie != null ? new AuthToken(authCookie) : null;
	}
	
}
