package com.jsg.hive.messages.types;

import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public class ChatSession {

	private WebSocketSession session;
	private UUID activeChatId;
	private User user;
	private UUID sessionId;
	
	public ChatSession(WebSocketSession session) {
		this.session = session;
		this.sessionId = UUID.fromString(session.getId());
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
	
	public void setUser(long id) {
		// TODO replace with something which calls the campus microservice
//		MySQLRepository<User> repo = new MySQLRepository<>(SQLTable.USERS);
//		List<User> results = repo.findWhereEqual(SQLColumn.ID, id, new UserBuilder());
//		if(results == null) {
//			user = null;
//			return;
//		}
//		user = results.get(0);
	}
	
}
