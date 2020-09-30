package com.jsg.postie.messages.api;

import com.jsg.postie.api.Version1Controller;
import com.jsg.postie.libs.httpexceptions.HttpException;
import com.jsg.postie.messages.service.MessageService;
import com.jsg.postie.messages.types.Message;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class MessageController extends Version1Controller {

    @GetMapping(value = "/chat/{chatId}/messages/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> get(
            @PathVariable("chatId") String chatId, @RequestParam(required = false) int limit) {
        if (chatId == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        try {
            List<Message> messages = MessageService.getMessages(chatId, limit);
            return ResponseEntity.ok(mapListToJson(messages));
        } catch (HttpException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getErrorMessage());
        }
    }

    @PostMapping(value = "/chat/{chatId}/messages/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public static ResponseEntity<String> save(@PathVariable("chatId") String chatId, @RequestBody Message message) {
        if (chatId == null || message == null)
            return BAD_REQUEST_HTTP_RESPONSE;
        MessageService.saveMessage(chatId, message);
        return EMPTY_OK_HTTP_RESPONSE;
    }

}
