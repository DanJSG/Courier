package com.jsg.courier.api.rest;

import java.util.ArrayList;
import java.util.List;

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
import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.Chat;
import com.jsg.courier.datatypes.ChatBuilder;
import com.jsg.courier.datatypes.ChatMember;
import com.jsg.courier.datatypes.ChatMemberBuilder;
import com.jsg.courier.datatypes.UserSession;
import com.jsg.courier.libs.sql.MySQLRepository;

@RestController
public class ChatController extends APIController {

	@Autowired
	public ChatController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret) {
		super(accessTokenSecret, client_id, client_secret);
	}
	
	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> create(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization, @RequestBody Chat chat) {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		chat.generateChatId();
		MySQLRepository<Chat> chatRepo = new MySQLRepository<>("chat.chats");
		if(!chatRepo.save(chat)) {
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		if(chat.getMembers().size() == 0) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		MySQLRepository<ChatMember> memberRepo = new MySQLRepository<>("chat.members");
		for(long memberId : chat.getMembers()) {
			memberRepo.save(new ChatMember(chat.getId(), memberId));
		}
		return ResponseEntity.status(HttpStatus.OK).body(chat.writeValueAsString());
	}
	
	@GetMapping(value = "/chat/getAll")
	public @ResponseBody ResponseEntity<String> getAll(@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt,
			@RequestHeader String authorization, @RequestParam long id) {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		// TODO optimise this bad implementation with a SQL joined view and change to data structure
		MySQLRepository<ChatMember> memberRepo = new MySQLRepository<>("chat.members");
		List<ChatMember> chatMembers = memberRepo.findWhereEqual("memberid", id, new ChatMemberBuilder());
		if(chatMembers == null || chatMembers.size() == 0) {
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}
		MySQLRepository<Chat> chatRepo = new MySQLRepository<>("chat.chats");
		List<Chat> chats = new ArrayList<>();
		for(ChatMember currentUser : chatMembers) {
			List<Chat> currentChat = chatRepo.findWhereEqual("chatid", currentUser.getChatId().toString(), new ChatBuilder());
			if(currentChat != null && currentChat.size() > 0) {
				chats.add(currentChat.get(0));
			}
		}
		if(chats.size() == 0) {
			return ResponseEntity.status(HttpStatus.OK).body(null);
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
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		MySQLRepository<ChatMember> memberRepo = new MySQLRepository<ChatMember>("chat.members");
		System.out.println(chatId);
		// TODO This all needs optimising as it is far too slow (~600-700ms for a chat with 7 members)
		// 		I should look at doing a number of things differently 
		//			-> Storing users within courier DB alongside auth provider and auth provider ID
		//			-> Use SQL views for easy lookups
		//			-> Modify or add to data structure to fit SQL view
		List<ChatMember> members = memberRepo.findWhereEqual("chatid", chatId, new ChatMemberBuilder());
		if(members == null || members.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		List<UserSession> users = new ArrayList<>();
		for(ChatMember member : members) {
			users.add(new UserSession(member.getMemberId(), CLIENT_ID, CLIENT_SECRET));
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
