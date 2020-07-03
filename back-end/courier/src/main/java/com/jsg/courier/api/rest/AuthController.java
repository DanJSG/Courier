package com.jsg.courier.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.httprequests.HttpResponse;
import com.jsg.courier.repositories.UserInfoAPIRepository;
import com.jsg.courier.utilities.JWTHandler;

@RestController
public class AuthController extends APIController {
	
	@Autowired
	public AuthController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret,
			@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenSecret, client_id, client_secret, sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/authorize")
	public @ResponseBody ResponseEntity<String> authorize(
			@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization) throws Exception {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		long id = JWTHandler.getIdFromToken(jwt);
		HttpResponse response = UserInfoAPIRepository.getUserInfo(id, CLIENT_ID, CLIENT_SECRET);
		if(response == null || response.getStatus() > 299 || response.getBody() == null) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
	}

}
