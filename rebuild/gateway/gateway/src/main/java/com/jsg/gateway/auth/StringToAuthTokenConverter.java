package com.jsg.gateway.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.jsg.gateway.auth.AuthToken;

@Component
public final class StringToAuthTokenConverter implements Converter<String, AuthToken>{
	
	public AuthToken convert(String header) {
		return new AuthToken((header.contains("Bearer")) ? getBearerToken(header) : header);
	}
	
	private static String getBearerToken(String authHeader) {
		if(authHeader == null)
			return null;
		String[] headerParts = authHeader.split("Bearer");
		if(headerParts.length != 2)
			return null;
		return headerParts[1].trim();
	}

}
