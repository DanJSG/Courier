package com.jsg.hive.messages.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.hive.auth.AuthToken;
import com.jsg.hive.chats.types.Chat;
import com.jsg.hive.chats.types.ChatSession;
import com.jsg.hive.messages.services.MessageService;
import com.jsg.hive.messages.types.Message;
import com.jsg.hive.messages.types.MessageBuilder;
import com.jsg.hive.messages.services.SessionService;
import com.jsg.hive.users.types.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Controller
public class MessageSocketController extends TextWebSocketHandler {

    private final String ACCESS_TOKEN_SECRET;

    @Autowired
    public MessageSocketController(@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret) {
        ACCESS_TOKEN_SECRET = accessTokenSecret;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage messageJson) throws Exception {
        String messageJsonPayload = messageJson.getPayload();
        UUID sessionId = UUID.fromString(session.getId());
        char firstChar = messageJsonPayload.charAt(0);
        switch(firstChar) {
            case '@':
                String chatsJson = messageJsonPayload.substring(1);
                List<Chat> chatList = Arrays.asList(new ObjectMapper().readValue(chatsJson, Chat[].class));
                SessionService.addChatSessions(chatList, sessionId);
                return;
            case '~':
                setActiveChat(sessionId, messageJsonPayload);
                return;
            case '#':
                SessionService.authorizeSession(sessionId, new AuthToken(messageJsonPayload.substring(1)), ACCESS_TOKEN_SECRET);
                session.sendMessage(new TextMessage("#"));
                return;
            default:
                Message message = new MessageBuilder().fromJson(messageJsonPayload);
                if (!persistMessage(sessionId, message))
                    return;
                broadcastMessage(message, sessionId);
        }
    }

    private void setActiveChat(UUID sessionId, String message) throws Exception {
        String chatIdJson = message.substring(1);
        UUID activeChatId = new ObjectMapper().readValue(chatIdJson, UUID.class);
        SessionService.setActiveChat(sessionId, activeChatId);
        broadcastSessions(activeChatId, null);
    }

    private boolean persistMessage(UUID sessionId, Message message) {
        if (!SessionService.isAuthorized(sessionId))
            return false;
        if (message.getChatId() == null)
            return false;
        if (!MessageService.saveMessage(message))
            return false;
        return true;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID sessionId = UUID.fromString(session.getId());
        if (SessionService.sessionExists(sessionId))
            return;
        SessionService.addSession(session, sessionId);
        System.out.println("WebSocket connection established between server and session with ID: " + session.getId() + ".");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        UUID sessionId = UUID.fromString(session.getId());
        List<UUID> chatIds = SessionService.removeChatSession(sessionId);
        Map<UUID, Boolean> contactedSessions = new HashMap<>();
        for(UUID chatId : chatIds) {
            broadcastSessions(chatId, contactedSessions);
        }
    }

    private void broadcastMessage(Message message, UUID sessionId) throws Exception {
        if (!SessionService.chatExists(message.getChatId()))
            return;
        Map<UUID, ChatSession> chatSessions = SessionService.getChat(message.getChatId());
        chatSessions.forEach((currentSessionId, currentSession) -> {
            if(currentSessionId.equals(sessionId))
                return; // equivalent to continue
            try {
                currentSession.getSession().sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void broadcastSessions(UUID chatId, @Nullable Map<UUID, Boolean> sessionsToSkip) throws Exception {
        ConcurrentMap<UUID, ChatSession> chatSessions = SessionService.getChat(chatId);
        List<User> users = SessionService.getAllUsersInChat(chatId);
        String jsonResponse = "`";
        jsonResponse += new ObjectMapper().writeValueAsString(users);
        for(ChatSession chatSession : chatSessions.values()) {
            if(sessionsToSkip != null) {
                if(sessionsToSkip.containsKey(chatSession.getSessionId()))
                    continue;
                sessionsToSkip.put(chatSession.getSessionId(), true);
            }
            chatSession.getSession().sendMessage(new TextMessage(jsonResponse));
        }
    }

}
