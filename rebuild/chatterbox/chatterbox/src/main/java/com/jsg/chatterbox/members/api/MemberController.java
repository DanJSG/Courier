package com.jsg.chatterbox.members.api;

import com.jsg.chatterbox.api.Version1Controller;
import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.libs.httpexceptions.HttpException;
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
        try {
            Member member = MemberService.getMember(id);
            return ResponseEntity.ok(member.writeValueAsString());
        } catch (HttpException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }
    }

    @PutMapping(value = "/member/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> update(Member member) {
        if (member == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        try {
            Member updatedMember = MemberService.updateMember(member);
            return ResponseEntity.ok(updatedMember.writeValueAsString());
        } catch (HttpException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }
    }

    @DeleteMapping(value = "/member/delete/{userId}")
    public static ResponseEntity<String> delete(@PathVariable("userId") long id) {
        if (id < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        try {
            MemberService.deleteMember(id);
            return EMPTY_OK_HTTP_RESPONSE;
        } catch (HttpException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }
    }

    @GetMapping(value = "/chat/{chatId}/members/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(@PathVariable("chatId") String id) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        try {
            List<Member> members = MemberService.getChatMembers(id);
            return ResponseEntity.ok(mapListToJson(members));
        } catch (HttpException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }
    }

    @PostMapping(value = "/chat/{chatId}/member/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> add(@PathVariable("chatId") String id, @RequestBody Member member) {
        if (id == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        try {
            Chat chat = MemberService.addMemberToChat(id, member);
            return ResponseEntity.ok(chat.writeValueAsString());
        } catch (HttpException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }

    }

    @DeleteMapping(value = "/chat/{chatId}/member/{userId}/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> remove(@PathVariable("chatId") String chatId, @PathVariable("userId") long userId) {
        if (chatId == null || userId < 0)
            return BAD_REQUEST_HTTP_RESPONSE;
        try {
            MemberService.removeMemberFromChat(chatId, userId);
            return EMPTY_OK_HTTP_RESPONSE;
        } catch (HttpException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }
    }

}
