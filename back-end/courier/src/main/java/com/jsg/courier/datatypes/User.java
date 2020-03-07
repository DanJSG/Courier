package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	
	@JsonProperty
	private String sessionId;
	
	@JsonProperty
	private String username;
	
	public User() {};
	
	public User(String sessionId, String username) {
		this.sessionId = sessionId;
		this.username = username;
	}
	
}
