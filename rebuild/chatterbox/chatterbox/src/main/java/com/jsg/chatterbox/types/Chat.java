package com.jsg.chatterbox.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntity;

public class Chat implements JsonObject, SQLEntity {
	
	@JsonProperty
	private UUID id;
	
	@JsonProperty
	private String name;

	@JsonCreator
	public Chat(@JsonProperty @Nullable UUID id, @JsonProperty String name) {
		this.name = name;
		this.id = id == null ? generateId() : id;
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public UUID generateId() {
		id = UUID.randomUUID();
		return id;
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

	@Override
	public Map<SQLColumn, Object> toSqlMap() {
		Map<SQLColumn, Object> map = new HashMap<>(2);
		map.put(SQLColumn.ID, id.toString());
		map.put(SQLColumn.NAME, name);
		return map;
	}
	
}
