package com.jsg.chatterbox.api;

import com.jsg.chatterbox.chats.service.ChatService;
import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.chats.types.EmptyChat;
import com.jsg.chatterbox.chats.types.EmptyChatBuilder;
import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;
import com.jsg.chatterbox.types.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController extends Version1Controller {

	@GetMapping(value = "/chat/get/{chatId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> get(@PathVariable("chatId") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		Chat chat = ChatService.getChat(id);
		return chat != null ? ResponseEntity.ok(chat.writeValueAsString()) : NOT_FOUND_HTTP_RESPONSE;
//		SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
//		SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
//		List<EmptyChat> emptyChats = chatRepo.findWhereEqual(SQLColumn.CHAT_ID, id, new EmptyChatBuilder());
//		List<Member> members = memberRepo.findWhereEqual(SQLColumn.CHAT_ID, id, new MemberBuilder());
//		if (emptyChats == null || members == null)
//			return NOT_FOUND_HTTP_RESPONSE;
//		EmptyChat emptyChat = emptyChats.get(0);
//		Chat chat = new Chat(emptyChat.getId(), emptyChat.getName(), members);
//		return ResponseEntity.status(HttpStatus.OK).body(chat.writeValueAsString());
	}

	@GetMapping(value = "/chats/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> get(@PathVariable("userId") long userId) {
		if (userId < 0)
			return BAD_REQUEST_HTTP_RESPONSE;
		List<EmptyChat> chats = ChatService.getUsersChats(userId);
		return mapListToJson(chats);
//		SQLRepository<EmptyChat> repo = new MySQLRepository<>(SQLTable.CHATS);
//		List<EmptyChat> chats = repo.findWhereEqual(SQLColumn.MEMBER_ID, userId, new EmptyChatBuilder());
//		return mapListToJson(chats);
	}

	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> create(@RequestBody Chat chat) {
		if (chat == null || chat.getMembers() == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		return ChatService.saveChat(chat) ? ResponseEntity.ok(chat.writeValueAsString()) : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
//		SQLRepository<Chat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
//		SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
//		for(Member member : chat.getMembers())
//			System.out.println(member.getUsername() + " [" + member.getId() + "]");
//		if (!chatRepo.save(chat) || !memberRepo.saveMany(chat.getMembers()))
//			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
//		return ResponseEntity.ok(chat.writeValueAsString());
	}

	@PutMapping(value = "/chat/update")
	public static ResponseEntity<String> update(EmptyChat chat) {
		if (chat == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<EmptyChat> repo = new MySQLRepository<>(SQLTable.DETAILS);
		if (!repo.updateWhereEquals(SQLColumn.CHAT_ID, chat.getId(), chat.toSqlMap()))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}

	@DeleteMapping(value = "/chat/delete/{chatId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> delete(@PathVariable("chatId") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
		SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
		if (!chatRepo.deleteWhereEquals(SQLColumn.CHAT_ID, id) || !memberRepo.deleteWhereEquals(SQLColumn.CHAT_ID, id))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}

}
