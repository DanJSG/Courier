package com.jsg.courier.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.MessageBuilder;
import com.jsg.courier.libs.nosql.MongoRepository;

@RestController
public class MessageController extends APIController {
	
	private final String MONGO_CONNECTION_STRING;
	private final String MONGO_DATABASE_NAME;
	
	@Autowired
	public MessageController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret,
			@Value("${mongo.connectionstring}") String mongoConnectionString,
			@Value("${mongo.database.name}") String mongoDbName) {
		super(accessTokenSecret, client_id, client_secret);
		this.MONGO_CONNECTION_STRING = mongoConnectionString;
		this.MONGO_DATABASE_NAME = mongoDbName;
	}

	@GetMapping(value = "/message/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAll(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestParam String chatId) {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		MongoRepository<Message> repo = new MongoRepository<>(MONGO_CONNECTION_STRING, MONGO_DATABASE_NAME);
		try {
			List<Message> messages = repo.findAll(chatId, new MessageBuilder());
			if(messages.size() == 0) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			}
			ObjectMapper mapper = new ObjectMapper();
			String jsonResponse = mapper.writeValueAsString(messages);
			System.out.println(jsonResponse);
			return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
		} catch (Exception e) {
			e.printStackTrace();
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
	}
	
}
