package com.jsg.hive.messages.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.hive.types.JsonObject;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class EmptyChat implements JsonObject {
	
	@JsonProperty
	private UUID id;
	
	@JsonProperty
	private String name;

	@JsonCreator
	public EmptyChat(@JsonProperty("id") @Nullable UUID id, @JsonProperty("name") String name) {
		this.name = name;
		this.id = id == null ? UUID.randomUUID() : id;
	}

	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String writeValueAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
