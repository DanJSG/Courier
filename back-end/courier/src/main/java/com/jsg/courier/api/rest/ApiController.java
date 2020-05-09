package com.jsg.courier.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1")
public abstract class ApiController {

	protected static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	protected static final String REFRESH_TOKEN_NAME = "ref.tok";
	protected static final String ACCESS_TOKEN_NAME = "acc.tok";
	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	protected static final ResponseEntity<String> BAD_REQUEST_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	
	protected final String ACCESS_TOKEN_SECRET;
	protected final String SQL_USERNAME;
	protected final String SQL_PASSWORD;
	protected final String SQL_CONNECTION_STRING;
	
	@Autowired
	protected ApiController(String accessTokenSecret, String sqlUsername,
			String sqlPassword, String sqlConnectionString) {
		this.ACCESS_TOKEN_SECRET = accessTokenSecret;
		this.SQL_CONNECTION_STRING = sqlConnectionString;
		this.SQL_USERNAME = sqlUsername;
		this.SQL_PASSWORD = sqlPassword;
	}
	
	protected ApiController(String sqlUsername, String sqlPassword, String sqlConnectionString) {
		this.ACCESS_TOKEN_SECRET = null;
		this.SQL_CONNECTION_STRING = sqlConnectionString;
		this.SQL_USERNAME = sqlUsername;
		this.SQL_PASSWORD = sqlPassword;
	}
	
}
