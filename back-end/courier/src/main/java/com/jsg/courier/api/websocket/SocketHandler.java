package com.jsg.courier.api.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.ChatDTO;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.UserSession;
import com.jsg.courier.datatypes.WebSocketHeaders;
import com.jsg.courier.repositories.MessageRepository;

@Service
public class SocketHandler extends TextWebSocketHandler {
	
	private static ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, WebSocketSession>> chats = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private final String CLIENT_ID;
	private final String CLIENT_SECRET;
	
	@Autowired
	public SocketHandler(@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret) {
				this.CLIENT_ID = client_id;
				this.CLIENT_SECRET = client_secret;
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage messageJson) throws Exception {
		String messageJsonPayload = messageJson.getPayload();
		if(messageJsonPayload.charAt(0) == '@') {
			addChatSession(session, messageJsonPayload);
			return;
		}
		Message message = objectMapper.readValue(messageJsonPayload, Message.class);
		MessageRepository repo = new MessageRepository();
		String collectionName = message.getChatId() != null ? message.getChatId().toString() : "messages";
		repo.save(message, collectionName);
		repo.closeConnection();
		broadcastMessage(message, UUID.fromString(session.getId()));
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if(sessions.containsKey(UUID.fromString(session.getId()))) {
			System.out.println("WebSocket connection already exists between server and session: " + session.getId());
			return;
		}
		sessions.put(UUID.fromString(session.getId()), session);
//		getChatHistory(session);
		System.out.println("WebSocket connection established between server and session with ID: " + session.getId() + ".");
		broadcastSessions();
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		List<UUID> emptyChatIds = new ArrayList<>();
		chats.forEach((currentChatId, currentSessionMap) -> {
			currentSessionMap.remove(UUID.fromString(session.getId()));
			if(currentSessionMap.size() == 0) {
				emptyChatIds.add(currentChatId);
			}
		});
		for(UUID chatId : emptyChatIds) {
			chats.remove(chatId);
		}
		sessions.remove(UUID.fromString(session.getId()));
		broadcastSessions();
	}
	
	private void broadcastMessage(Message message, UUID sessionId) throws Exception {
		if(!chats.containsKey(message.getChatId())) {
			return;
		}
		Map<UUID, WebSocketSession> chatSessions = chats.get(message.getChatId());
		chatSessions.forEach((currentSessionId, currentSession) -> {
			if(currentSessionId.equals(sessionId)) {
				return;
			}
			try {
				currentSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		});
	}
	
	private void broadcastSessions() throws Exception {
		String json = "`[";
		int i = 0;
		Set<Long> idSet = new HashSet<>();
		for(WebSocketSession session : sessions.values()) {
			WebSocketHeaders headers = new WebSocketHeaders(session);
			if(idSet.contains(headers.getId())) {
				continue;
			}
			idSet.add(headers.getId());
			if(i != 0) {
				json += ",";
			}
			json += (new TextMessage(objectMapper.writeValueAsString(new UserSession(headers.getId(), CLIENT_ID, CLIENT_SECRET)))).getPayload();
			i++;
		}
		json += "]";
		System.out.println(json);
		for(WebSocketSession session : sessions.values()) {
			session.sendMessage(new TextMessage(json));
		}
	}
	
	private void addChatSession(WebSocketSession session, String receivedChats) {
		String chatsJson = receivedChats.substring(1);
		System.out.println(chatsJson);
		List<ChatDTO> chatList;
		try {
			chatList = Arrays.asList(objectMapper.readValue(chatsJson, ChatDTO[].class));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
		for(ChatDTO chat : chatList) {
			if(!chats.containsKey(chat.getId())) {
				ConcurrentHashMap<UUID, WebSocketSession> newSessionMap = new ConcurrentHashMap<>();
				chats.put(chat.getId(), newSessionMap);
			}
			chats.get(chat.getId()).put(UUID.fromString(session.getId()), session);
		}
	}
	
}
