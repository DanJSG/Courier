package com.jsg.chatterbox.libs.sql;

import java.util.Map;

public interface SQLEntity {
	
	Map<SQLColumn, Object> toSqlMap();
	
}
