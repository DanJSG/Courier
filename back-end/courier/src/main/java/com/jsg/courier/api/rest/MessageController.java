package com.jsg.courier.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsg.courier.constants.OAuth2;

public class MessageController extends ApiController {

	@Autowired
	public MessageController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret,
			@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenSecret, client_id, client_secret, sqlUsername, sqlPassword, sqlConnectionString);
	}

	@GetMapping(value = "/messages/getAll")
	public ResponseEntity<String> getAll(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestParam String chatId) {
		return UNAUTHORIZED_HTTP_RESPONSE;
	}
	
}
