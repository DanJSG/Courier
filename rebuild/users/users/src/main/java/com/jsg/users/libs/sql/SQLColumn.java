package com.jsg.users.libs.sql;

import java.util.HashMap;
import java.util.Map;

public enum SQLColumn implements Whitelist {
	
	CHATID, ID, MEMBERID, NAME, USERNAME;
	
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
