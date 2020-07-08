package com.jsg.courier.datatypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.libs.nosql.JsonObject;
import com.jsg.courier.libs.sql.SQLEntity;

public class Chat implements SQLEntity, JsonObject {
	
	@JsonProperty
	private UUID id;
	
	@JsonProperty
	@JsonInclude(Include.NON_NULL)
	private String name;
	
	@JsonProperty
	@JsonInclude(Include.NON_NULL)
	private List<Long> members;
	
	@JsonCreator
	private Chat() {};
	
	public Chat(UUID id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Chat(String name) {
		this(UUID.randomUUID(), name);
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Long> getMembers() {
		return this.members;
	}
	
	public void generateChatId() {
		setChatId(UUID.randomUUID());
	}
	
	public void setChatId(UUID id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMembers(List<Long> members) {
		this.members = members;
	}
	
	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("chatid", this.id.toString());
		map.put("name", this.name);
		return map;
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
