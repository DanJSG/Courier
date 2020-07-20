package com.jsg.courier.api.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.Chat;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.MessageBuilder;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.libs.nosql.MongoRepository;

@Service
public class SocketHandler extends TextWebSocketHandler {

	//							   Chat ID               Session ID    Session
	private static ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, ChatSession>> chats = new ConcurrentHashMap<>();
	
	//                             Session ID     Session
	private static ConcurrentHashMap<UUID, ChatSession> sessions = new ConcurrentHashMap<>();
	
	@Autowired
	public SocketHandler() {}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage messageJson) throws Exception {
		String messageJsonPayload = messageJson.getPayload();
		UUID sessionId = UUID.fromString(session.getId());
		if(messageJsonPayload.charAt(0) == '@') {
			addChatSession(session, messageJsonPayload);
			return;
		} else if(messageJsonPayload.charAt(0) == '~') {
			String chatIdJson = messageJsonPayload.substring(1);
			UUID activeChatId = new ObjectMapper().readValue(chatIdJson, UUID.class);
			sessions.get(sessionId).setActiveChatId(activeChatId);
			broadcastSessions(activeChatId, null);
			return;	
		}
		Message message = new MessageBuilder().fromJson(messageJsonPayload);
		if(message.getChatId() == null) {
			return;
		}
		MongoRepository<Message> repo = new MongoRepository<>();
		String collectionName = message.getChatId().toString();
		repo.save(message, collectionName);
		broadcastMessage(message, sessionId);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		UUID sessionId = UUID.fromString(session.getId());
		if(sessions.containsKey(sessionId)) {
			System.out.println("WebSocket connection already exists between server and session: " + session.getId());
			return;
		}
		sessions.put(sessionId, new ChatSession(session));
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
	
	private void addChatSession(WebSocketSession session, String receivedChats) {
		String chatsJson = receivedChats.substring(1);
		UUID sessionId = UUID.fromString(session.getId());
		System.out.println(chatsJson);
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
