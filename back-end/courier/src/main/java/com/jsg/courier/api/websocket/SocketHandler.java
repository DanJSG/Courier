package com.jsg.courier.api.websocket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.UserSession;
import com.jsg.courier.datatypes.WebSocketHeaders;
import com.jsg.courier.repositories.MessageRepository;

@Service
public class SocketHandler extends TextWebSocketHandler {
		
	private static ConcurrentHashMap<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();
		
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage messageJson) throws Exception {
		Message message = objectMapper.readValue(messageJson.getPayload(), Message.class);
		message.print();
		MessageRepository repo = new MessageRepository();
		repo.save(message, "messages");
		repo.closeConnection();
		broadcastMessage(message, UUID.fromString(session.getId()));
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WebSocketHeaders headers = new WebSocketHeaders(session);	
		if(sessions.containsKey(UUID.fromString(session.getId()))) {
			System.out.println("WebSocket connection already exists between server and session: " + session.getId());
			return;
		}
		sessions.put(UUID.fromString(session.getId()), session);
		getChatHistory(session);
		System.out.println("WebSocket connection established between server and session with details: " + session.getId() + ", " + headers.getId());
		broadcastSessions();
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		if(sessions.remove(UUID.fromString(session.getId())) == null) {
			System.out.println("Failed to close WebSocket connection. Could not find session with ID " + session.getId());
			return;
		};
		System.out.println("WebSocket connection closed.");
		broadcastSessions();
	}
	
	private void broadcastMessage(Message message, UUID sessionId) throws Exception {
		System.out.println("(broadcast)Session size is: " + sessions.size());
		sessions.forEach((currentSessionId, currentSession) -> {
			if(currentSessionId.equals(sessionId)) {
				System.out.println("Not sending to session: " + currentSessionId);
				// this works similarly to continue in a foreach lambda function
				return;
			}
			try {
				currentSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		});
	}
	
	private void broadcastSessions() throws Exception {
		String json = "`[";
		int i = 0;
		Set<Long> idSet = new HashSet<Long>();
		for(WebSocketSession session : sessions.values()) {
			WebSocketHeaders headers = new WebSocketHeaders(session);
			if(idSet.contains(headers.getId())) {
				continue;
			}
			idSet.add(headers.getId());
			if(i != 0) {
				json += ",";
			}
			json += (new TextMessage(objectMapper.writeValueAsString(new UserSession(headers.getId(), "token-goes-here")))).getPayload();
			i++;
		}
		json += "]";
		System.out.println(json);
		for(WebSocketSession session : sessions.values()) {
			session.sendMessage(new TextMessage(json));
		}
	}
	
	private void getChatHistory(WebSocketSession session) throws Exception {
		MessageRepository repo = new MessageRepository();
		List<Message> messages = repo.findAll("messages");
		repo.closeConnection();
		for(Message message : messages) {
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
		}
	}
	
}
