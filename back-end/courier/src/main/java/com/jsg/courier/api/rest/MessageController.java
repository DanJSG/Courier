package com.jsg.courier.api.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.AuthToken;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.MessageBuilder;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.libs.nosql.MongoRepository;
import com.jsg.courier.libs.sql.MySQLRepository;
import com.jsg.courier.libs.sql.SQLColumn;
import com.jsg.courier.libs.sql.SQLRepository;
import com.jsg.courier.libs.sql.SQLTable;

@RestController
public class MessageController extends APIController {
	
	@Autowired
	public MessageController(
			@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret,
			@Value("${OAUTH2_CLIENT_ID}") String client_id, 
			@Value("${OAUTH2_CLIENT_SECRET}") String client_secret) {
		super(accessTokenSecret, client_id, client_secret);
	}

	@GetMapping(value = "/message/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAll(@RequestHeader AuthToken authorization, @RequestParam String chatId) {
		long id = authorization.getId();
		MongoRepository<Message> repo = new MongoRepository<>();
		SQLRepository<User> userRepo = new MySQLRepository<>(SQLTable.CHATSFULL);
		List<User> users = userRepo.findWhereEqual(Arrays.asList(SQLColumn.CHATID, SQLColumn.ID), Arrays.asList(chatId, id), new UserBuilder());
		if(users == null) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		try {
			List<Message> messages = repo.findAll(chatId, new MessageBuilder());
			if(messages.size() == 0) {
				return NO_CONTENT_HTTP_RESPONSE;
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
