package com.jsg.courier.helpers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.jsg.courier.auth.AuthHeaderHandler;
import com.jsg.courier.datatypes.AuthToken;

@Component
public final class StringToAuthTokenConverter implements Converter<String, AuthToken>{

	@Override
	public AuthToken convert(String authHeader) {
		return new AuthToken((authHeader.contains("Bearer")) ? AuthHeaderHandler.getBearerToken(authHeader) : authHeader);
	}

}
