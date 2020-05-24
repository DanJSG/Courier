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

import com.jsg.courier.httprequests.HttpResponse;
import com.jsg.courier.repositories.UserInfoAPIRepository;
import com.jsg.courier.utilities.AuthHeaderHandler;
import com.jsg.courier.utilities.JWTHandler;

@RestController
public class AuthController extends ApiController {
	
	@Autowired
	public AuthController(@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenSecret, sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/authorize")
	public @ResponseBody ResponseEntity<String> authorize(@CookieValue(name = ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization) throws Exception {
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		System.out.println(headerJwt);
		System.out.println(jwt);
		if(!JWTHandler.tokenIsValid(jwt, ACCESS_TOKEN_SECRET) || 
				!JWTHandler.tokenIsValid(headerJwt, ACCESS_TOKEN_SECRET)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		long id = JWTHandler.getIdFromToken(headerJwt);
		HttpResponse response = UserInfoAPIRepository.getUserInfo(id);
		return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
	}

}
