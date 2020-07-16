package com.jsg.courier.datatypes;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.libs.nosql.JsonObject;
import com.jsg.courier.libs.sql.SQLEntity;

public class User implements SQLEntity, JsonObject {
	
	@JsonProperty
	private long id;
	
	private long oauthId;
	
	@JsonProperty("displayName")
	private String username;
	
	@JsonCreator
	private User() {}
	
	public User(long id, long oauthId, String username) {
		this.id = id;
		this.oauthId = oauthId;
		this.username = username;
	}

	public User(long oauthId, String username) {
		this.oauthId = oauthId;
		this.username = username;
	}

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("oauthid", oauthId);
		map.put("username", username);
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
