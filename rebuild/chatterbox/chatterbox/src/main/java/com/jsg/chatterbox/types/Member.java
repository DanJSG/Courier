package com.jsg.chatterbox.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntity;

public class Member implements SQLEntity {

	@JsonProperty
	private long id;
	
	@JsonProperty
	private String username;
	
	@JsonCreator
	private Member() {}
	
	@Override
	public Map<SQLColumn, Object> toSqlMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
