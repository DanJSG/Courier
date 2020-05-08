package com.jsg.courier.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.UserInfo;
import com.jsg.courier.repositories.UserInfoRepository;
import com.jsg.courier.utilities.AuthHeaderHandler;
import com.jsg.courier.utilities.JWTHandler;

@RestController
public final class UserController extends ApiController {
	
	@Autowired
	public UserController(@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenSecret, sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/findUserInfoById")
	public @ResponseBody ResponseEntity<String> findUserInfoById(@RequestParam long searchId, @RequestParam long id, 
			@CookieValue(required = false) String jwt, @RequestHeader String authorization) throws Exception {
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(jwt, ACCESS_TOKEN_SECRET) || 
				!JWTHandler.tokenIsValid(headerJwt, ACCESS_TOKEN_SECRET)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized.");
		}
		UserInfoRepository repo = new UserInfoRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		List<UserInfo> userInfoList = repo.findWhereEqual("id", id, 1);
		if(userInfoList == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to find user with requested ID");
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(userInfoList.get(0)));
	}
	
	@PostMapping(value = "/verifyJwt")
	public @ResponseBody ResponseEntity<Boolean> verifyJwt(@RequestParam Long id, @CookieValue(required = false) String jwt, 
			@RequestHeader String authorization) {
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(jwt, ACCESS_TOKEN_SECRET) || 
				!JWTHandler.tokenIsValid(headerJwt, ACCESS_TOKEN_SECRET)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
}
