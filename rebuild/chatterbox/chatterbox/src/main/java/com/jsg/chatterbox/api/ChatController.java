package com.jsg.chatterbox.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;
import com.jsg.chatterbox.types.Chat;
import com.jsg.chatterbox.types.ChatBuilder;

@RestController
public class ChatController extends Version1Controller implements RestApi<Chat, String> {

	@Override
	@GetMapping(value = "/chat/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> get(@PathVariable("id") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<Chat> repo = new MySQLRepository<>(SQLTable.CHATS);
		List<Chat> chats = repo.findWhereEqual(SQLColumn.ID, id, new ChatBuilder());
		if (chats == null) 
			return NOT_FOUND_HTTP_RESPONSE;
		String chatResponse = chats.get(0).writeValueAsString();
		if (chatResponse == null)
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return ResponseEntity.status(HttpStatus.OK).body(chatResponse);
	}

	@Override
	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> post(@RequestBody Chat chat) {
		if (chat == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		if (chat.getId() != null)
			chat.generateId();
		SQLRepository<Chat> repo = new MySQLRepository<>(SQLTable.CHATS);
		if (!repo.save(chat))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return ResponseEntity.status(HttpStatus.OK).body(chat.writeValueAsString());
	}

	@Override
	@PutMapping(value = "/chat/update")
	public ResponseEntity<String> put(Chat chat) {
		if (chat == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<Chat> repo = new MySQLRepository<>(SQLTable.CHATS);
		if (!repo.updateWhereEquals(SQLColumn.ID, chat.getId().toString(), chat.toSqlMap()))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}

	@Override
	@DeleteMapping(value = "/chat/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<Chat> repo = new MySQLRepository<>(SQLTable.CHATS);
		if (!repo.deleteWhereEquals(SQLColumn.ID, id))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		// TODO delete associated members too
		return EMPTY_OK_HTTP_RESPONSE;
	}

}
