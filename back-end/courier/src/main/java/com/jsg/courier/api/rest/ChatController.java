package com.jsg.courier.api.rest;

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

import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.Chat;
import com.jsg.courier.datatypes.ChatDTO;
import com.jsg.courier.datatypes.ChatMember;
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
			@RequestHeader String authorization, @RequestBody ChatDTO receivedChat) {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		Chat chat = new Chat(receivedChat.getName());
		receivedChat.setChatId(chat.getId());
		MySQLRepository<Chat> chatRepo = new MySQLRepository<>(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD, "chat.chats");
		chatRepo.openConnection();
		if(!chatRepo.save(chat)) {
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		chatRepo.closeConnection();
		if(receivedChat.getMembers().size() == 0) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		MySQLRepository<ChatMember> memberRepo = new MySQLRepository<>(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD, "chat.members");
		memberRepo.openConnection(); 
		for(long memberId : receivedChat.getMembers()) {
			memberRepo.save(new ChatMember(chat.getId(), memberId));
		}
		memberRepo.closeConnection();
		return ResponseEntity.status(HttpStatus.OK).body(receivedChat.writeValueAsString());
	}

}
