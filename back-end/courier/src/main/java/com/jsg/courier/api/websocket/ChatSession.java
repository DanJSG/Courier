package com.jsg.courier.api.websocket;

import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

public class ChatSession {

	private WebSocketSession session;
	private UUID activeChatId;
	
	public ChatSession(WebSocketSession session) {
		this.session = session;
		activeChatId = null;
	}
	
	public WebSocketSession getSession() {
		return session;
	}
	
	public UUID getActiveChatId() { 
		return activeChatId;
	}
	
	public void setActiveChatId(UUID id) {
		this.activeChatId = id;
	}
	
}
