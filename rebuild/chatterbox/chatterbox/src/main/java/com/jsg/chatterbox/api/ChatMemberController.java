package com.jsg.chatterbox.api;

import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;
import com.jsg.chatterbox.types.Member;
import com.jsg.chatterbox.types.MemberBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class ChatMemberController extends Version1Controller {

    @GetMapping(value = "/chat/{chatId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("chatId") String id) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        SQLRepository<Member> repo = new MySQLRepository<>(SQLTable.CHATS);
        List<Member> members = repo.findWhereEqual(SQLColumn.CHAT_ID, id, new MemberBuilder());
        return mapListToJson(members);
    }

    @GetMapping(value = "/member/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("userId") long id) {
        if (id < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        List<Member> members = memberSQLRepository.findWhereEqual(SQLColumn.MEMBER_ID, id, 0, new MemberBuilder());
        if (members == null)
            return NOT_FOUND_HTTP_RESPONSE;
        return ResponseEntity.ok(members.get(0).writeValueAsString());
    }

    @PostMapping(value = "/chat/{chatId}/member/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> add(@PathVariable("chatId") String id, @RequestBody Member member) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        member.setAssociatedChatId(id);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (memberRepo.findWhereEqual(SQLColumn.CHAT_ID, id, new MemberBuilder()) == null)
            return NOT_FOUND_HTTP_RESPONSE;
        if (!memberRepo.save(member))
            return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
        return ChatController.get(id);
    }
    
    @PutMapping(value = "/member/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> update(Member member) {
        if (member == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberRepo.updateWhereEquals(SQLColumn.MEMBER_ID, member.getId(), member.toSqlMap()))
            return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
        return get(member.getId());
    }

    @PutMapping
    public static ResponseEntity<String> put(Member body) {
        return METHOD_NOT_ALLOWED_HTTP_RESPONSE;
    }

    @DeleteMapping
    public static ResponseEntity<String> delete(Long param) {
        return METHOD_NOT_ALLOWED_HTTP_RESPONSE;
    }

}
