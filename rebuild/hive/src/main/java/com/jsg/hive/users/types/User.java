package com.jsg.hive.users.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.hive.types.JsonObject;

public class User implements JsonObject {

	@JsonProperty
	private long id;
	
	@JsonProperty
	private String username;
	
	@JsonCreator
	private User() {}
	
	public User(long id, String username) {
		this.id = id;
		this.username = username;
	}
	
	public long getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}

	@Override
	public String writeValueAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
