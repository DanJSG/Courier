package com.jsg.chatterbox.libs.sql;

import java.util.Map;

public interface SQLEntity {
	
	public Map<SQLColumn, Object> toSqlMap();
	
}
