package com.jsg.courier.datatypes;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatDTO implements DTO {
	
	@JsonProperty
	private UUID id;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private List<Long> members;
	
	public UUID getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Long> getMembers() {
		return this.members;
	}
	
	public void setChatId(UUID id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
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
