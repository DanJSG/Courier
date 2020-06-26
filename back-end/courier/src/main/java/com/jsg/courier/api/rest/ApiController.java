package com.jsg.courier.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jsg.courier.utilities.AuthHeaderHandler;
import com.jsg.courier.utilities.JWTHandler;

@RequestMapping("/api/v1")
public abstract class ApiController {

	protected static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	protected static final ResponseEntity<String> BAD_REQUEST_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	protected static final ResponseEntity<String> INTERNAL_SERVER_ERROR_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	
	protected final String ACCESS_TOKEN_SECRET;
	protected final String CLIENT_ID;
	protected final String CLIENT_SECRET;
	protected final String SQL_USERNAME;
	protected final String SQL_PASSWORD;
	protected final String SQL_CONNECTION_STRING;
	
	@Autowired
	protected ApiController(String accessTokenSecret, String client_id, String client_secret, 
			String sqlUsername, String sqlPassword, String sqlConnectionString) {
		ACCESS_TOKEN_SECRET = accessTokenSecret;
		CLIENT_ID = client_id;
		CLIENT_SECRET = client_secret;
		SQL_CONNECTION_STRING = sqlConnectionString;
		SQL_USERNAME = sqlUsername;
		SQL_PASSWORD = sqlPassword;
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
