package com.jsg.courier.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jsg.courier.utilities.AuthHeaderHandler;
import com.jsg.courier.utilities.JWTHandler;

@RequestMapping("/api/v1")
public abstract class APIController {

	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	protected static final ResponseEntity<String> BAD_REQUEST_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	protected static final ResponseEntity<String> INTERNAL_SERVER_ERROR_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	protected static final ResponseEntity<String> NO_CONTENT_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	
	protected final String ACCESS_TOKEN_SECRET;
	protected final String CLIENT_ID;
	protected final String CLIENT_SECRET;
	
	@Autowired
	protected APIController(String accessTokenSecret, String client_id, String client_secret) {
		ACCESS_TOKEN_SECRET = accessTokenSecret;
		CLIENT_ID = client_id;
		CLIENT_SECRET = client_secret;
	}
	
	protected Boolean tokensAreValid(String authHeader, String jwtCookie) {
		String headerJwt = AuthHeaderHandler.getBearerToken(authHeader);
		if(!JWTHandler.tokenIsValid(jwtCookie, ACCESS_TOKEN_SECRET) || 
				!JWTHandler.tokenIsValid(headerJwt, ACCESS_TOKEN_SECRET)) {
			return false;
		}
		return true;
	}
	
}
