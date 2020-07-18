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
import com.jsg.courier.datatypes.Chat;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.MessageBuilder;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.datatypes.UserSession;
import com.jsg.courier.datatypes.WebSocketHeaders;
import com.jsg.courier.libs.nosql.MongoRepository;
import com.jsg.courier.libs.sql.MySQLRepository;

@Service
public class SocketHandler extends TextWebSocketHandler {
	
	private static ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, WebSocketSession>> chats = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, List<UUID>> sessionChats = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
	
	private final String CLIENT_ID;
	private final String CLIENT_SECRET;
	
	@Autowired
	public SocketHandler(
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret,
			@Value("${mongo.database.name}") String mongoDbName) {
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
		Message message = new MessageBuilder().fromJson(messageJsonPayload);
		if(message.getChatId() == null) {
			return;
		}
		MongoRepository<Message> repo = new MongoRepository<>();
		String collectionName = message.getChatId().toString();
		repo.save(message, collectionName);
		broadcastMessage(message, UUID.fromString(session.getId()));
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		UUID sessionId = UUID.fromString(session.getId());
		if(sessions.containsKey(sessionId)) {
			System.out.println("WebSocket connection already exists between server and session: " + session.getId());
			return;
		}
		sessions.put(sessionId, session);
//		getChatHistory(session);
		System.out.println("WebSocket connection established between server and session with ID: " + session.getId() + ".");
		broadcastSessions(sessionId);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		List<UUID> emptyChatIds = new ArrayList<>();
		UUID sessionId = UUID.fromString(session.getId());
		chats.forEach((currentChatId, currentSessionMap) -> {
			currentSessionMap.remove(sessionId);
			if(currentSessionMap.size() == 0) {
				emptyChatIds.add(currentChatId);
			}
		});
		for(UUID chatId : emptyChatIds) {
			chats.remove(chatId);
		}
		sessions.remove(sessionId);
		broadcastSessions(sessionId);
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
				currentSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		});
	}
	
	private void broadcastSessions(UUID sessionId) throws Exception {
		
		// TODO sort this shitty innefficient mess out
		List<UUID> chatIds = sessionChats.get(sessionId);
		String membersJson = "`";
		for(UUID id : chatIds) {
			ConcurrentHashMap<UUID, WebSocketSession> activeChatSessions = chats.get(id);
			List<User> currentUserList = new ArrayList<>();
			for(WebSocketSession session : activeChatSessions.values()) {
				WebSocketHeaders headers = new WebSocketHeaders(session);
				MySQLRepository<User> userRepo = new MySQLRepository<>("users");
				List<User> userResult = userRepo.findWhereEqual("oauthid", headers.getId(), 1, new UserBuilder());
				if(userResult == null || userResult.size() == 0) {
					continue;
				}
				User user = userResult.get(0);
				currentUserList.add(user);
			}
			membersJson += new ObjectMapper().writeValueAsString(currentUserList);
			for(WebSocketSession session : activeChatSessions.values()) {
				session.sendMessage(new TextMessage(membersJson));
			}
		}
		
//		String json = "`[";
//		int i = 0;
//		Set<Long> idSet = new HashSet<>();
//		for(WebSocketSession session : sessions.values()) {
//			WebSocketHeaders headers = new WebSocketHeaders(session);
//			if(idSet.contains(headers.getId())) {
//				continue;
//			}
//			idSet.add(headers.getId());
//			if(i != 0) {
//				json += ",";
//			}
//			json += (new TextMessage(new ObjectMapper().writeValueAsString(new UserSession(headers.getId(), CLIENT_ID, CLIENT_SECRET)))).getPayload();
//			i++;
//		}
//		json += "]";
//		System.out.println(json);
//		for(WebSocketSession session : sessions.values()) {
//			session.sendMessage(new TextMessage(json));
//		}
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
		List<UUID> chatIds = new ArrayList<>();
		for(Chat chat : chatList) {
			chatIds.add(chat.getId());
		}
		sessionChats.put(sessionId, chatIds);
		for(Chat chat : chatList) {
			if(!chats.containsKey(chat.getId())) {
				ConcurrentHashMap<UUID, WebSocketSession> newSessionMap = new ConcurrentHashMap<>();
				chats.put(chat.getId(), newSessionMap);
			}
			chats.get(chat.getId()).put(sessionId, session);
		}
	}
	
}
