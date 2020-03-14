package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String email;
	
	public User(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
