package com.jsg.chatterbox.chats.api;

import com.jsg.chatterbox.api.Version1Controller;
import com.jsg.chatterbox.chats.service.ChatService;
import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.chats.types.EmptyChat;
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
	}

	@GetMapping(value = "/chats/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> get(@PathVariable("userId") long userId) {
		if (userId < 0)
			return BAD_REQUEST_HTTP_RESPONSE;
		List<EmptyChat> chats = ChatService.getUsersChats(userId);
		return mapListToJson(chats);
	}

	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> create(@RequestBody Chat chat) {
		if (chat == null || chat.getMembers() == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		return ChatService.saveChat(chat) ? ResponseEntity.ok(chat.writeValueAsString()) : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
	}

	@PutMapping(value = "/chat/update")
	public static ResponseEntity<String> update(EmptyChat chat) {
		if (chat == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		return ChatService.renameExistingChat(chat) ? EMPTY_OK_HTTP_RESPONSE : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
	}

	@DeleteMapping(value = "/chat/delete/{chatId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> delete(@PathVariable("chatId") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		return ChatService.deleteExistingChat(id) ? EMPTY_OK_HTTP_RESPONSE : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
	}

}
