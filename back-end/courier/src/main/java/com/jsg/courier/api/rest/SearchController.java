package com.jsg.courier.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.libs.sql.MySQLRepository;
import com.jsg.courier.libs.sql.SQLRepository;

@RestController
public class SearchController extends APIController {

	protected SearchController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret) {
		super(accessTokenSecret, client_id, client_secret);
	}
	
	@GetMapping(value = "/search/searchUsers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> searchUsers(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestParam String searchTerm) {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		String searchQuery = searchTerm + "%";
		SQLRepository<User> repo = new MySQLRepository<User>("Users");
		List<User> users = repo.findWhereLike("username", searchQuery, new UserBuilder());
		if(users == null) {
			return NO_CONTENT_HTTP_RESPONSE;
		}
		for(User user : users) {
			System.out.println(user.writeValueAsString());
		}
		try {
			String jsonList = new ObjectMapper().writeValueAsString(users);
			return ResponseEntity.status(HttpStatus.OK).body(jsonList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}	
	}
	
}
