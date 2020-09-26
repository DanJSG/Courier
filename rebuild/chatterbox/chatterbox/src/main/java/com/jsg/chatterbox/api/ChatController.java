package com.jsg.chatterbox.api;

import java.sql.SQLRecoverableException;
import java.util.List;

import com.jsg.chatterbox.types.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;

@RestController
public class ChatController extends Version1Controller {

	@GetMapping(value = "/chat/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> get(@PathVariable("id") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
		SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
		List<EmptyChat> emptyChats = chatRepo.findWhereEqual(SQLColumn.ID, id, new EmptyChatBuilder());
		List<Member> members = memberRepo.findWhereEqual(SQLColumn.CHAT_ID, id, new MemberBuilder());
		if (emptyChats == null || members == null)
			return NOT_FOUND_HTTP_RESPONSE;
		EmptyChat emptyChat = emptyChats.get(0);
		Chat chat = new Chat(emptyChat.getId(), emptyChat.getName(), members);
		return ResponseEntity.status(HttpStatus.OK).body(chat.writeValueAsString());
	}

	@PostMapping(value = "/chat/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> create(@RequestBody Chat chat) {
		if (chat == null || chat.getMembers() == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<Chat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
		SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
		for(Member member : chat.getMembers())
			System.out.println(member.getUsername() + " [" + member.getId() + "]");
		if (!chatRepo.save(chat) || !memberRepo.saveMany(chat.getMembers()))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return ResponseEntity.ok(chat.writeValueAsString());
	}

	@PutMapping(value = "/chat/update")
	public ResponseEntity<String> update(EmptyChat chat) {
		if (chat == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<EmptyChat> repo = new MySQLRepository<>(SQLTable.DETAILS);
		if (!repo.updateWhereEquals(SQLColumn.ID, chat.getId(), chat.toSqlMap()))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}

	@DeleteMapping(value = "/chat/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		if (id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
		SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
		if (!chatRepo.deleteWhereEquals(SQLColumn.ID, id) || !memberRepo.deleteWhereEquals(SQLColumn.CHAT_ID, id))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}

}
