package com.jsg.courier.libs.sql;

public enum SQLTable {
	
	CHAT_MEMBERS("chatmembers"),
	CHATS("chats"),
	USERS("users"),
	CHATS_VIEW("chatsfull");
	
	private String name;
	
	SQLTable(String tableName) {
		name = tableName;
	}
	
	public String getName() {
		return name;
	}
	
}
