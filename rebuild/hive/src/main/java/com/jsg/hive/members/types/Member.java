package com.jsg.hive.members.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.hive.types.JsonObject;

import java.util.UUID;

public class Member implements JsonObject {

	@JsonProperty
	private long id;
	
	@JsonProperty
	private String username;

	private UUID associatedChatId;
	
	@JsonCreator
	public Member(@JsonProperty("id") long id, @JsonProperty("username") String username) {
		this.id = id;
		this.username = username;
		this.associatedChatId = null;
	}

	public String getUsername() {
		return username;
	}

	public void setAssociatedChatId(UUID associatedChatId) {
		this.associatedChatId = associatedChatId;
	}

	public void setAssociatedChatId(String associatedChatId) {
		this.associatedChatId = UUID.fromString(associatedChatId);
	}

	public long getId() {
		return id;
	}

	@Override
	public String writeValueAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
