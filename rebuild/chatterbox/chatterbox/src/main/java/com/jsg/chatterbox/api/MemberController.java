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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberController extends Version1Controller {

    @GetMapping(value = "/member/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("userId") long id) {
        if (id < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        List<Member> members = memberSQLRepository.findWhereEqual(SQLColumn.MEMBER_ID, id, 0, new MemberBuilder());
        if (members == null)
            return NOT_FOUND_HTTP_RESPONSE;
        return ResponseEntity.ok(members.get(0).writeValueAsString());
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

    @DeleteMapping(value = "/member/delete/{userId}")
    public static ResponseEntity<String> delete(@PathVariable("userId") long id) {
        if (id < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberSQLRepository.deleteWhereEquals(SQLColumn.MEMBER_ID, id))
            return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
        return EMPTY_OK_HTTP_RESPONSE;
    }

    @GetMapping(value = "/chat/{chatId}/members/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("chatId") String id) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        SQLRepository<Member> repo = new MySQLRepository<>(SQLTable.CHATS);
        List<Member> members = repo.findWhereEqual(SQLColumn.CHAT_ID, id, new MemberBuilder());
        return mapListToJson(members);
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

    @DeleteMapping(value = "/chat/{chatId}/member/{userId}/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> remove(@PathVariable("chatId") String chatId, @PathVariable("userId") long userId) {
        if (chatId == null || userId < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        Map<SQLColumn, Object> removalConditions = new HashMap<>();
        removalConditions.put(SQLColumn.CHAT_ID, chatId);
        removalConditions.put(SQLColumn.MEMBER_ID, userId);
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberSQLRepository.deleteWhereEquals(removalConditions))
            return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
        return EMPTY_OK_HTTP_RESPONSE;
    }

}
