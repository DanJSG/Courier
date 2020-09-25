package com.jsg.chatterbox.libs.sql;

import java.util.HashMap;
import java.util.Map;

public enum SQLColumn implements Whitelist {
	
	ID, NAME, ENTRY, MEMBER_ID, CHAT_ID;
	
	private static final Map<String, SQLColumn> mapping = new HashMap<>(16);
	
	static {
		for(SQLColumn column : SQLColumn.values()) {
			mapping.put(column.name(), column);
		}
	}

	@Override
	public boolean validate(String value) {
		return mapping.containsKey(value);
	}
	
}
