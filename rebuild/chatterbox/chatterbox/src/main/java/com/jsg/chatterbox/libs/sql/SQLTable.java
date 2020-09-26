package com.jsg.chatterbox.libs.sql;

import java.util.HashMap;
import java.util.Map;

public enum SQLTable implements Whitelist {

	CHATS, DETAILS, MEMBERS;
	
	private static final Map<String, SQLTable> mappings = new HashMap<>(16);
	
	static {
		for(SQLTable table : SQLTable.values()) {
			mappings.put(table.name(), table);
		}
	}

	@Override
	public boolean validate(String value) {
		return mappings.containsKey(value);
	}
	
}
