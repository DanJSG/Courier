package com.jsg.hive.messages.services;

import com.jsg.hive.auth.AuthToken;
import com.jsg.hive.chats.types.Chat;
import com.jsg.hive.chats.types.ChatSession;
import com.jsg.hive.users.types.User;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionService {

    //							<Chat ID, <Session ID, Session>>
    private static ConcurrentMap<UUID, ConcurrentMap<UUID, ChatSession>> chats = new ConcurrentHashMap<>();

    //                          <Session ID, Session>
    private static ConcurrentMap<UUID, ChatSession> sessions = new ConcurrentHashMap<>();

    public static ConcurrentMap<UUID, ChatSession> getSessions() {
        return sessions;
    }

    public static ConcurrentMap<UUID, ConcurrentMap<UUID, ChatSession>> getChats() {
        return chats;
    }

    public static ConcurrentMap<UUID, ChatSession> getChat(UUID id) {
        return chats.get(id);
    }

    public static void addSession(WebSocketSession session, UUID sessionId) {
        sessions.put(sessionId, new ChatSession(session));
    }

    public static boolean isAuthorized(UUID sessionId) {
        return sessions.get(sessionId).isAuthorized();
    }

    public static boolean sessionExists(UUID sessionId) {
        return sessions.containsKey(sessionId);
    }

    public static boolean chatExists(UUID chatId) {
        return chats.containsKey(chatId);
    }

    public static void authorizeSession(UUID sessionId, AuthToken token, String tokenSecret) {
        if (!token.verify(tokenSecret))
            return;
        sessions.get(sessionId).setAuthorized(true);
        sessions.get(sessionId).setUser(token.getId());
    }

    public static void addChatSessions(List<Chat> chatList, UUID sessionId) {
        if (!isAuthorized(sessionId))
            return;
        for (Chat chat : chatList) {
            if (!chats.containsKey(chat.getId())) {
                ConcurrentMap<UUID, ChatSession> sessionMap = new ConcurrentHashMap<>();
                chats.put(chat.getId(), sessionMap);
            }
            chats.get(chat.getId()).put(sessionId, sessions.get(sessionId));
        }
    }

    public static void setActiveChat(UUID sessionId, UUID chatId) {
        if (!isAuthorized(sessionId))
            return;
        sessions.get(sessionId).setActiveChatId(chatId);
        if (!chats.containsKey(chatId)) {
            ConcurrentMap<UUID, ChatSession> newSubscriber = new ConcurrentHashMap<>();
            newSubscriber.put(sessionId, sessions.get(sessionId));
            chats.put(chatId, newSubscriber);
        }
    }

    public static List<User> getAllUsersInChat(UUID chatId) {
        Set<User> users = new HashSet<>();
        for (ChatSession session : chats.get(chatId).values())
            users.add(session.getUser());
        return Arrays.asList((User[]) users.toArray());
    }

    public static List<UUID> removeChatSession(UUID sessionId) {
        List<UUID> emptyChatIds = new ArrayList<>();
        List<UUID> chatIds = new ArrayList<>();
        chats.forEach((currentChatId, currentSessionMap) -> {
            if(currentSessionMap.containsKey(sessionId))
                chatIds.add(currentChatId);
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
        return chatIds;
    }

}
