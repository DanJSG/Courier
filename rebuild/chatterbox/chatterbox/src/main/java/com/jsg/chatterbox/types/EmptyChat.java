package com.jsg.chatterbox.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntity;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmptyChat implements JsonObject, SQLEntity {
	
	@JsonProperty
	private UUID id;
	
	@JsonProperty
	private String name;

	@JsonCreator
	public EmptyChat(@JsonProperty @Nullable UUID id, @JsonProperty String name) {
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

	@Override
	public Map<SQLColumn, Object> toSqlMap() {
		Map<SQLColumn, Object> map = new HashMap<>(2);
		map.put(SQLColumn.CHAT_ID, id.toString());
		map.put(SQLColumn.NAME, name);
		return map;
	}
	
}
