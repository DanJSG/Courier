package com.jsg.courier.datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jsg.courier.libs.sql.SQLEntity;

public class Chat implements SQLEntity {
	
	private UUID id;
	private String name;
	
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
	
	@Override
	public Map<String, Object> toHashMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("chatid", this.id.toString());
		map.put("name", this.name);
		return map;
	}

}
