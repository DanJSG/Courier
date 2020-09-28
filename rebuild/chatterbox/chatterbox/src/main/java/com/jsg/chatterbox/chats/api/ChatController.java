package com.jsg.chatterbox.chats.api;

import com.jsg.chatterbox.api.Version1Controller;
import com.jsg.chatterbox.chats.service.ChatService;
import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.chats.types.EmptyChat;
import com.jsg.chatterbox.libs.httpexceptions.HttpException;
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
		try {
			Chat chat = ChatService.getChat(id);
			return ResponseEntity.ok(chat.writeValueAsString());
		} catch (HttpException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
		}
	}

	@GetMapping(value = "/chats/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> get(@PathVariable("userId") long userId) {
		if (userId < 0)
			return BAD_REQUEST_HTTP_RESPONSE;
		try {
			List<EmptyChat> chats = ChatService.getUsersChats(userId);
			return ResponseEntity.ok(mapListToJson(chats));
		} catch (HttpException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
		}
	}

	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> create(@RequestBody Chat chat) {
		if (chat == null || chat.getMembers() == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		try {
			ChatService.saveChat(chat);
			return ResponseEntity.ok(chat.writeValueAsString());
		} catch (HttpException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
		}
	}

	@PutMapping(value = "/chat/update", consumes = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> update(EmptyChat chat) {
		if (chat == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		try {
			ChatService.renameExistingChat(chat);
			return EMPTY_OK_HTTP_RESPONSE;
		} catch (HttpException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
		}
	}

	@DeleteMapping(value = "/chat/delete/{chatId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public static ResponseEntity<String> delete(@PathVariable("chatId") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		try {
			ChatService.deleteExistingChat(id);
			return EMPTY_OK_HTTP_RESPONSE;
		} catch (HttpException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
		}
	}

}
