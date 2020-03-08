package com.jsg.courier.api.websocket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.Message;
import com.jsg.courier.datatypes.User;

@Component
public class SocketHandler extends TextWebSocketHandler {
	
	private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	private List<String> sessionsIdList = new CopyOnWriteArrayList<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage messageJson) throws Exception {
		Message message = objectMapper.readValue(messageJson.getPayload(), Message.class);
		message.print();
		broadcastMessage(message);
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");		
		if(sessionsIdList.contains(headers[0])) {
			System.out.println("Already connected.");
			return;
		}
		sessions.add(session);
		sessionsIdList.add(headers[0]);
		System.out.println("WebSocket connection established between server and session with details: " + headers[0] + ", " + headers[1]);
		broadcastSessions();
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");		
		sessions.remove(session);
		sessionsIdList.remove(headers[0]);
		System.out.println("WebSocket connection closed.");
	}
	
	private void broadcastMessage(Message message) throws Exception {
		System.out.println("(broadcast)Session size is: " + sessions.size());
		for(WebSocketSession session : sessions) {
			String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");
			System.out.println("Session ID is: " + headers[0]);
			if(headers[0].equals(message.getId())) {
				System.out.println("Not sending to session: " + session.getHandshakeHeaders().getFirst("sec-websocket-protocol"));
				continue;
			}
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
		}
	}
	
	private void broadcastSessions() throws Exception {
		String json = "`[";
		for(int i=0; i<sessions.size(); i++) {
			String[] headers = sessions.get(i).getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");		
			json += (new TextMessage(objectMapper.writeValueAsString(new User(headers[0], headers[1])))).getPayload();
			if(i != sessions.size() - 1) {
				json += ",";
			}
		}
		json += "]";
		System.out.println(json);
		for(WebSocketSession session : sessions) {
			session.sendMessage(new TextMessage(json));
		}
	}
}
