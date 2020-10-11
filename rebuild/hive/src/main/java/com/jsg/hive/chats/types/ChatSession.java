package com.jsg.hive.chats.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.hive.libs.http.HttpRequestBuilder;
import com.jsg.hive.libs.http.HttpResponse;
import com.jsg.hive.users.types.User;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public class ChatSession {

	private WebSocketSession session;
	private UUID activeChatId;
	private User user;
	private UUID sessionId;
	private boolean authorized;
	
	public ChatSession(WebSocketSession session) {
		this.session = session;
		this.sessionId = UUID.fromString(session.getId());
		activeChatId = null;
		authorized = false;
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

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authStatus) {
		authorized = authStatus;
	}

	public UUID getSessionId() {
		return sessionId;
	}
	
	public void setActiveChatId(UUID id) {
		this.activeChatId = id;
	}
	
	public void setUser(long id) {
		// TODO add some kind of auth header in here
		// TODO refactor URL into environment variable
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder("http://campus:8081/api/v1/user/get/" + id);
		String userJson;
		try {
			HttpResponse response = new HttpResponse(requestBuilder.toHttpURLConnection());
			if (response.getStatus() != 200)
				throw new Exception();
			userJson = response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			user = null;
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			user = mapper.readValue(userJson, User.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			user = null;
		}
	}
	
}
