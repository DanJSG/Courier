package com.jsg.courier.api.websocket;

import java.util.List;
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
		broadcastMessage(message);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WebSocketHeaders headers = new WebSocketHeaders(session);	
		if(sessions.containsKey(headers.getSessionId())) {
			System.out.println("WebSocket connection already exists between server and session: " + headers.getSessionId());
			return;
		}
		sessions.put(headers.getSessionId(), session);
		getChatHistory(session);
		System.out.println("WebSocket connection established between server and session with details: " + headers.getSessionId() + ", " + headers.getId());
		broadcastSessions();
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		WebSocketHeaders headers = new WebSocketHeaders(session);
		if(sessions.remove(headers.getSessionId()) == null) {
			System.out.println("Failed to close WebSocket connection. Could not find session with ID " + headers.getSessionId());
			return;
		};
		System.out.println("WebSocket connection closed.");
		broadcastSessions();
	}
	
	private void broadcastMessage(Message message) throws Exception {
		System.out.println("(broadcast)Session size is: " + sessions.size());
		sessions.forEach((sessionId, session) -> {
			if(sessionId.equals(message.getSessionId())) {
				System.out.println("Not sending to session: " + sessionId);
				// this works similarly to continue in a foreach lambda function
				return;
			}
			try {
				session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		});
	}
	
	private void broadcastSessions() throws Exception {
		String json = "`[";
		int i = 0;
		for(WebSocketSession session : sessions.values()) {
			WebSocketHeaders headers = new WebSocketHeaders(session);
			json += (new TextMessage(objectMapper.writeValueAsString(new UserSession(headers.getId(), headers.getSessionId(), "token-goes-here")))).getPayload();
			if(i != sessions.size() - 1) {
				json += ",";
			}
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
