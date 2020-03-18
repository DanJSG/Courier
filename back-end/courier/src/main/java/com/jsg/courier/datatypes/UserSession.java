package com.jsg.courier.datatypes;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSession {
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private UUID sessionId;
	
	@JsonProperty
	private String token;
	
	public UserSession() {}
	
	public UserSession(long id, String token) {
		this.id = id;
		this.sessionId = UUID.randomUUID();
		System.out.println(this.sessionId);
		this.token = "TOKEN";
	}
	
	public UserSession(long id, UUID sessionId, String token) {
		this.id = id;
		this.sessionId = sessionId;
		this.token = token;
	}
	
}
