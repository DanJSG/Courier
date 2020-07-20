package com.jsg.courier.api.websocket;

import java.util.List;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.datatypes.WebSocketHeaders;
import com.jsg.courier.libs.sql.MySQLRepository;

public class ChatSession {

	private WebSocketSession session;
	private UUID activeChatId;
	private User user;
	
	public ChatSession(WebSocketSession session) {
		this.session = session;
		activeChatId = null;
		setUser(new WebSocketHeaders(session).getId());
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
	
	public void setActiveChatId(UUID id) {
		this.activeChatId = id;
	}
	
	private void setUser(long id) {
		MySQLRepository<User> repo = new MySQLRepository<>("users");
		List<User> results = repo.findWhereEqual("id", id, new UserBuilder());
		if(results == null) {
			user = null;
			return;
		}
		user = results.get(0);
	}
	
}
