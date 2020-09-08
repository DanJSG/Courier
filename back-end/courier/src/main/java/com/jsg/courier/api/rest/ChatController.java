package com.jsg.courier.api.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.auth.JWTHandler;
import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.Chat;
import com.jsg.courier.datatypes.ChatBuilder;
import com.jsg.courier.datatypes.ChatMember;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.libs.sql.MySQLRepository;
import com.jsg.courier.libs.sql.SQLTable;

@RestController
public class ChatController extends APIController {

	@Autowired
	public ChatController(
			@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret,
			@Value("${OAUTH2_CLIENT_ID}") String client_id, 
			@Value("${OAUTH2_CLIENT_SECRET}") String client_secret) {
		super(accessTokenSecret, client_id, client_secret);
	}
	
	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> create(
			@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestBody Chat chat) {
		chat.generateChatId();
		Set<Long> uniqueMembers = new HashSet<>(chat.getMembers());
		chat.setMembers(new ArrayList<>(uniqueMembers));
		MySQLRepository<Chat> chatRepo = new MySQLRepository<>(SQLTable.CHATS);
		if(!chatRepo.save(chat)) {
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		if(chat.getMembers().size() == 0) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		MySQLRepository<ChatMember> memberRepo = new MySQLRepository<>(SQLTable.CHAT_MEMBERS);
		List<ChatMember> members = new ArrayList<>();
		for(long memberId : chat.getMembers()) {
			members.add(new ChatMember(chat.getId(), memberId));
		}
		memberRepo.saveMany(members);
		System.out.println("CHAT JSON OBJECT:");
		System.out.println(chat.writeValueAsString());
		return ResponseEntity.status(HttpStatus.OK).body(chat.writeValueAsString());
	}
	
	@GetMapping(value = "/chat/getAll")
	public @ResponseBody ResponseEntity<String> getAll(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt,
			@RequestHeader String authorization) {
		long id = JWTHandler.getIdFromToken(authorization);
		MySQLRepository<Chat> chatRepo = new MySQLRepository<>(SQLTable.CHATS_VIEW);
		List<Chat> chats = chatRepo.findWhereEqual("id", id, new ChatBuilder());
		if(chats == null || chats.size() == 0) {
			return NO_CONTENT_HTTP_RESPONSE;
		}
		ObjectMapper mapper = new ObjectMapper();
		String responseJson;
		try {
			responseJson = mapper.writeValueAsString(chats);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		return ResponseEntity.status(HttpStatus.OK).body(responseJson);
	}
	
	@GetMapping(value = "/chat/getMembers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMembers(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestParam String chatId) {
		long id = JWTHandler.getIdFromToken(authorization);
		MySQLRepository<User> userRepo = new MySQLRepository<>(SQLTable.CHATS_VIEW);
		// TODO implement SQL select ordering to avoid using linear search for user ID 
		List<User> users = userRepo.findWhereEqual("chatid", chatId, new UserBuilder());
		boolean userFound = false;
		for(User user : users) {
			if(user.getId() == id) {
				userFound = true;
				break;
			}
		}
		if(!userFound) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		if(users == null || users.size() == 0) {
			return NO_CONTENT_HTTP_RESPONSE;
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(users));
		} catch(Exception e) {
			e.printStackTrace();
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
	}

}
