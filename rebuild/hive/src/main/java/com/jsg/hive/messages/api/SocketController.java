package com.jsg.hive.messages.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.hive.auth.AuthToken;
import com.jsg.hive.chats.types.Chat;
import com.jsg.hive.chats.types.ChatSession;
import com.jsg.hive.messages.types.*;
import com.jsg.hive.users.types.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SocketController extends TextWebSocketHandler {


    //							<Chat ID, <Session ID, Session>>
    private static ConcurrentMap<UUID, ConcurrentHashMap<UUID, ChatSession>> chats = new ConcurrentHashMap<>();

    //                          <Session ID, Session>
    private static ConcurrentMap<UUID, ChatSession> sessions = new ConcurrentHashMap<>();

    //							<Session ID, authorized?>
    private static ConcurrentMap<UUID, Boolean> sessionAuth = new ConcurrentHashMap<>();

    private final String ACCESS_TOKEN_SECRET;

    @Autowired
    public SocketController(@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret) {
        ACCESS_TOKEN_SECRET = accessTokenSecret;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage messageJson) throws Exception {
        String messageJsonPayload = messageJson.getPayload();
        UUID sessionId = UUID.fromString(session.getId());
        char firstChar = messageJsonPayload.charAt(0);
        switch(firstChar) {
            case '@':
                addChatSession(session, sessionId, messageJsonPayload);
                return;
            case '~':
                registerActiveChat(sessionId, messageJsonPayload);
                return;
            case '#':
                authorizeSession(session, sessionId, messageJsonPayload);
                return;
            default:
                if(!sessionAuth.get(sessionId)) {
                    return;
                }
                Message message = new MessageBuilder().fromJson(messageJsonPayload);
                if(message.getChatId() == null) {
                    return;
                }
                // TODO replace this with a call to postie service
//                MongoRepository<Message> repo = new MongoRepository<>();
//                String collectionName = message.getChatId().toString();
//                repo.save(message, collectionName);
                broadcastMessage(message, sessionId);
        }
    }

    private void registerActiveChat(UUID sessionId, String message) throws Exception {
        if(!sessionAuth.get(sessionId)) {
            return;
        }
        String chatIdJson = message.substring(1);
        UUID activeChatId = new ObjectMapper().readValue(chatIdJson, UUID.class);
        sessions.get(sessionId).setActiveChatId(activeChatId);
        if(!chats.containsKey(activeChatId)) {
            ConcurrentHashMap<UUID, ChatSession> newSub = new ConcurrentHashMap<>();
            newSub.put(sessionId, sessions.get(sessionId));
            chats.put(activeChatId, newSub);
        }
        broadcastSessions(activeChatId, null);
    }

    private void authorizeSession(WebSocketSession session, UUID sessionId, String message) throws Exception {
        AuthToken authToken = new AuthToken(message.substring(1));
        if(!authToken.verify(ACCESS_TOKEN_SECRET)) {
            return;
        }
        sessionAuth.put(sessionId, true);
        sessions.get(sessionId).setUser(authToken.getId());
        session.sendMessage(new TextMessage("#"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UUID sessionId = UUID.fromString(session.getId());
        if(sessions.containsKey(sessionId)) {
            System.out.println("WebSocket connection already exists between server and session: " + session.getId());
            return;
        }
        sessions.put(sessionId, new ChatSession(session));
        sessionAuth.put(sessionId, false);
        System.out.println("WebSocket connection established between server and session with ID: " + session.getId() + ".");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        List<UUID> emptyChatIds = new ArrayList<>();
        UUID sessionId = UUID.fromString(session.getId());
        List<UUID> chatIds = new ArrayList<>();
        chats.forEach((currentChatId, currentSessionMap) -> {
            if(currentSessionMap.containsKey(sessionId)) {
                chatIds.add(currentChatId);
            }
            currentSessionMap.remove(sessionId);
            if(currentSessionMap.size() == 0) {
                emptyChatIds.add(currentChatId);
                chatIds.remove(currentChatId);
            }
        });
        for(UUID chatId : emptyChatIds) {
            chats.remove(chatId);
        }
        sessions.remove(sessionId);
        Map<UUID, Boolean> contactedSessions = new HashMap<>();
        for(UUID chatId : chatIds) {
            broadcastSessions(chatId, contactedSessions);
        }
    }

    private void broadcastMessage(Message message, UUID sessionId) throws Exception {
        if(!chats.containsKey(message.getChatId())) {
            return;
        }
        Map<UUID, ChatSession> chatSessions = chats.get(message.getChatId());
        chatSessions.forEach((currentSessionId, currentSession) -> {
            if(currentSessionId.equals(sessionId)) {
                return; //equivalent to continue
            }
            try {
                currentSession.getSession().sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
    }

    private void broadcastSessions(UUID chatId, @Nullable Map<UUID, Boolean> sessionsToSkip) throws Exception {
        ConcurrentHashMap<UUID, ChatSession> chatSessions = chats.get(chatId);
        Set<User> users = new HashSet<>();
        for(ChatSession chatSession : chatSessions.values()) {
            users.add(chatSession.getUser());
        }
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
        return;
    }

    private void addChatSession(WebSocketSession session, UUID sessionId, String receivedChats) {
        if(!sessionAuth.get(sessionId)) {
            return;
        }
        String chatsJson = receivedChats.substring(1);
        List<Chat> chatList;
        try {
            chatList = Arrays.asList(new ObjectMapper().readValue(chatsJson, Chat[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        for(Chat chat : chatList) {
            if(!chats.containsKey(chat.getId())) {
                ConcurrentHashMap<UUID, ChatSession> newSessionMap = new ConcurrentHashMap<>();
                chats.put(chat.getId(), newSessionMap);
            }
            chats.get(chat.getId()).put(sessionId, sessions.get(sessionId));
        }
    }

}
