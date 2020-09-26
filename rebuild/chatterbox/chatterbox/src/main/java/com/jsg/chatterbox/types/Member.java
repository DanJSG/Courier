package com.jsg.chatterbox.types;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntity;

public class Member implements SQLEntity {

	@JsonProperty
	private long id;
	
	@JsonProperty
	private String username;

	private UUID associatedChatId;
	
	@JsonCreator
	public Member(@JsonProperty long id, @JsonProperty String username) {
		this.id = id;
		this.username = username;
		this.associatedChatId = null;
	}

	public String getUsername() {
		return username;
	}

	public void setAssociatedChatId(UUID associatedChatId) {
		this.associatedChatId = associatedChatId;
	}

	public long getId() {
		return id;
	}

	@Override
	public Map<SQLColumn, Object> toSqlMap() {
		Map<SQLColumn, Object> map = new HashMap<>();
		map.put(SQLColumn.CHAT_ID, associatedChatId.toString());
		map.put(SQLColumn.MEMBER_ID, id);
		map.put(SQLColumn.USERNAME, username);
		return map;
	}

}
