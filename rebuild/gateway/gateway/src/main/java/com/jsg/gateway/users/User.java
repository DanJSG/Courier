package com.jsg.gateway.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.gateway.helpers.JsonObject;

public class User implements JsonObject {

	@JsonProperty
	private long id;
	
	@JsonProperty("displayName")
	private String username;
	
	@JsonCreator
	private User() {}
	
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
