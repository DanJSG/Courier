package com.jsg.users.libs.sql;

import java.util.Map;

public interface SQLEntity {
	
	public Map<SQLColumn, Object> toSqlMap();
	
}
