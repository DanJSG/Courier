package com.jsg.courier.api.websocket;

import java.util.List;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.datatypes.WebSocketHeaders;
import com.jsg.courier.libs.sql.MySQLRepository;
import com.jsg.courier.libs.sql.SQLTable;

public class ChatSession {

	private WebSocketSession session;
	private UUID activeChatId;
	private User user;
	private UUID sessionId;
	
	public ChatSession(WebSocketSession session) {
		this.session = session;
		this.sessionId = UUID.fromString(session.getId());
		setUser(new WebSocketHeaders(session).getId());
		activeChatId = null;
	}
	
	public WebSocketSession getSession() {
		return session;
	}
	
	public UUID getActiveChatId() { 
		return activeChatId;
	}
	
	public User getUser() {
		return user;
	}
	
	public UUID getSessionId() {
		return sessionId;
	}
	
	public void setActiveChatId(UUID id) {
		this.activeChatId = id;
	}
	
	private void setUser(long id) {
		MySQLRepository<User> repo = new MySQLRepository<>(SQLTable.USERS);
		List<User> results = repo.findWhereEqual("id", id, new UserBuilder());
		if(results == null) {
			user = null;
			return;
		}
		user = results.get(0);
	}
	
}
