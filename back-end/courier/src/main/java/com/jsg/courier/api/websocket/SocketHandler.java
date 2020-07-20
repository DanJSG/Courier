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

	//							   Chat ID               Session ID    Session
	private static ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, ChatSession>> chats = new ConcurrentHashMap<>();
	
	//                             Session ID     Session
	private static ConcurrentHashMap<UUID, ChatSession> sessions = new ConcurrentHashMap<>();
	
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
		UUID sessionId = UUID.fromString(session.getId());
		if(messageJsonPayload.charAt(0) == '@') {
			addChatSession(session, messageJsonPayload);
			return;
		} else if(messageJsonPayload.charAt(0) == '~') {
			String chatIdJson = messageJsonPayload.substring(1);
			UUID activeChatId = new ObjectMapper().readValue(chatIdJson, UUID.class);
			sessions.get(sessionId).setActiveChatId(activeChatId);
			broadcastSessions(activeChatId);
//			System.out.println("Received/parsed data: ");
//			System.out.println(messageJsonPayload);
//			System.out.println(activeChatId.toString());
//			System.out.println("Stored data: ");
//			System.out.println("From sessions: " + 
//								sessions.get(sessionId).getActiveChatId().toString() + 
//								"; " + sessions.get(sessionId).getSession().getId());
//			
//			chats.forEach((chatId, chatSessions) -> {
//				for(ChatSession currSession : chatSessions.values()) {
//					System.out.println("From chat " + chatId.toString() + ": " + 
//									   currSession.getActiveChatId() + 
//									   "; " + currSession.getSession().getId());
//				}
//			});
//			for(ConcurrentHashMap<UUID, ChatSession> chatSessions : chats.values()) {
//				for(ChatSession currSession : chatSessions.values()) {
//					System.out.println("From chats (pubsub queue): " + 
//									   currSession.getActiveChatId() + 
//									   "; " + currSession.getSession().getId());
//				}
//			}
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
//		getChatHistory(session);
		System.out.println("WebSocket connection established between server and session with ID: " + session.getId() + ".");
//		broadcastSessions(sessionId);
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
		for(UUID chatId : chatIds) {
			broadcastSessions(chatId);
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
	
	private void broadcastSessions(UUID chatId) throws Exception {
		
		ConcurrentHashMap<UUID, ChatSession> chatSessions = chats.get(chatId);
		Set<User> users = new HashSet<>();
		MySQLRepository<User> repo = new MySQLRepository<>("users");
		WebSocketHeaders header;
		for(ChatSession chatSession : chatSessions.values()) {
			header = new WebSocketHeaders(chatSession.getSession());
			List<User> results = repo.findWhereEqual("id", header.getId(), 1, new UserBuilder());
			if(results == null) 
				continue;
			User user = results.get(0);
			users.add(user);
		}
		String jsonResponse = "`";
		jsonResponse += new ObjectMapper().writeValueAsString(users);
		System.out.println(jsonResponse);
		for(ChatSession chatSession : chatSessions.values()) {
			chatSession.getSession().sendMessage(new TextMessage(jsonResponse));
		}
		return;
		
		// Get list of chat IDs used by session
		
		// Build set of users from all users within the chats used by the session
		
		// Loop through 
		
		// loop over each chat ID in the list
		// loop over each chat member in the the current chat and add them to a set
		
//		ChatID
//			SessionID -> user IDs
//				
//		
//		
//		
//		
//		
		
		// TODO sort this shitty innefficient mess out
//		List<UUID> chatIds = sessionChats.get(sessionId);
//		if(chatIds == null) return;
//		String membersJson;
//		for(UUID id : chatIds) {
//			membersJson = "`";
//			ConcurrentHashMap<UUID, WebSocketSession> activeChatSessions = chats.get(id);
//			List<User> currentUserList = new ArrayList<>();
//			Set<User> userSet = new HashSet<>();
//			for(WebSocketSession session : activeChatSessions.values()) {
//				WebSocketHeaders headers = new WebSocketHeaders(session);
//				System.out.println("ID from header:" + headers.getId());
//				MySQLRepository<User> userRepo = new MySQLRepository<>("users");
//				List<User> userResult = userRepo.findWhereEqual("id", headers.getId(), 1, new UserBuilder());
//				if(userResult == null || userResult.size() == 0) {
//					continue;
//				}
//				User user = userResult.get(0);
//				currentUserList.add(user);
//			}
//			membersJson += new ObjectMapper().writeValueAsString(currentUserList);
//			System.out.println(membersJson);
//			for(WebSocketSession session : activeChatSessions.values()) {
//				if(!session.getId().contentEquals(sessionId.toString())) {
//					session.sendMessage(new TextMessage(membersJson));
//				}			
//			}
//		}
		
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
		for(Chat chat : chatList) {
			if(!chats.containsKey(chat.getId())) {
				ConcurrentHashMap<UUID, ChatSession> newSessionMap = new ConcurrentHashMap<>();
				chats.put(chat.getId(), newSessionMap);
			}
			chats.get(chat.getId()).put(sessionId, sessions.get(sessionId));
		}
	}
	
}
