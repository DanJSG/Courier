package com.jsg.campus.types;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.campus.libs.sql.SQLColumn;
import com.jsg.campus.libs.sql.SQLEntity;

public class User implements SQLEntity, JsonObject {

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
	public Map<SQLColumn, Object> toSqlMap() {
		Map<SQLColumn, Object> map = new HashMap<>(2);
		map.put(SQLColumn.ID, id);
		map.put(SQLColumn.USERNAME, username);
		return map;
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
