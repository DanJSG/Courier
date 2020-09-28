package com.jsg.chatterbox.members.api;

import com.jsg.chatterbox.api.Version1Controller;
import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.members.service.MemberService;
import com.jsg.chatterbox.members.types.Member;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class MemberController extends Version1Controller {

    @GetMapping(value = "/member/get/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("userId") long id) {
        if (id < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        Member member = MemberService.getMember(id);
        return member != null ? ResponseEntity.ok(member.writeValueAsString()) : NOT_FOUND_HTTP_RESPONSE;
    }

    @PutMapping(value = "/member/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> update(Member member) {
        if (member == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        Member updatedMember = MemberService.updateMember(member);
        return updatedMember != null ? ResponseEntity.ok(member.writeValueAsString()) : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
    }

    @DeleteMapping(value = "/member/delete/{userId}")
    public static ResponseEntity<String> delete(@PathVariable("userId") long id) {
        if (id < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        return MemberService.deleteMember(id) ? EMPTY_OK_HTTP_RESPONSE : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
    }

    @GetMapping(value = "/chat/{chatId}/members/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("chatId") String id) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        List<Member> members = MemberService.getChatMembers(id);
        return members != null ? mapListToJson(members) : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
    }

    @PostMapping(value = "/chat/{chatId}/member/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> add(@PathVariable("chatId") String id, @RequestBody Member member) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        Chat chat = MemberService.addMemberToChat(id, member);
        return chat != null ? ResponseEntity.ok(chat.writeValueAsString()) : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
    }

    @DeleteMapping(value = "/chat/{chatId}/member/{userId}/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> remove(@PathVariable("chatId") String chatId, @PathVariable("userId") long userId) {
        if (chatId == null || userId < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        return MemberService.removeMemberFromChat(chatId, userId) ? EMPTY_OK_HTTP_RESPONSE : INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
    }

}
