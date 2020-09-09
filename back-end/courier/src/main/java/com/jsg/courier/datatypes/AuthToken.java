package com.jsg.courier.datatypes;

import com.jsg.courier.auth.JWTHandler;

public class AuthToken {
	
	private String token;
	private long id;
	private String name;
	
	public AuthToken(String token) {
		this.token = token;
		id = JWTHandler.getIdFromToken(token);
		name = JWTHandler.getNameFromToken(token);
	}
	
	public String getToken() {
		return token;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}
	
}
