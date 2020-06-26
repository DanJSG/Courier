package com.jsg.courier.api.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.Chat;
import com.jsg.courier.libs.sql.MySQLRepository;

@RestController
public class ChatController extends ApiController {

	@Autowired
	public ChatController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret,
			@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenSecret, client_id, client_secret, sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> create(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestBody Map<String, String> body) {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		Chat chat = new Chat(body.get("name"));
		MySQLRepository<Chat> repo = new MySQLRepository<>(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD, "chat.chats");
		repo.openConnection();
		repo.save(chat);
		Map<String, String> chatJsonMap = new HashMap<>();
		chatJsonMap.put("id", chat.getId().toString());
		chatJsonMap.put("name", chat.getName());
		String jsonMap;
		try {
			jsonMap = new ObjectMapper().writeValueAsString(chatJsonMap);
			return ResponseEntity.status(HttpStatus.OK).body(jsonMap);
		} catch(Exception e) {
			e.printStackTrace();
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		// TODO implement members
//		List<String> members = body.get("members");
//		if(members.size() == 0) {
//			return BAD_REQUEST_HTTP_RESPONSE;
//		}
	}

}
