package com.jsg.courier.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1")
public abstract class ApiController {

	protected static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	protected static final String ACCESS_TOKEN_NAME = "acc.tok";
	protected static final String CLIENT_ID = "ThpDT2t2EDlO";
	protected static final String CLIENT_SECRET = "aU3LCC1vzagDBDqEX7O729rJpgStVkH9";
	protected static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	protected static final ResponseEntity<String> BAD_REQUEST_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	
	protected final String ACCESS_TOKEN_SECRET;
	
	@Autowired
	protected ApiController(String accessTokenSecret) {
		this.ACCESS_TOKEN_SECRET = accessTokenSecret;
	}
	
}
