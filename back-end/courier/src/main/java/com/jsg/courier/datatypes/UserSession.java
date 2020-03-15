package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSession {
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private int sessionId;
	
	@JsonProperty
	private String token;
	
	public UserSession() {}
	
	public UserSession(int sessionId, String token) {
		this.sessionId = sessionId;
		this.token = token;
	}
	
}
