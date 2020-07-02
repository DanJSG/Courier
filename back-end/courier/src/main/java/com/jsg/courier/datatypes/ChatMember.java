package com.jsg.courier.datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jsg.courier.libs.sql.SQLEntity;

public class ChatMember implements SQLEntity {

	private UUID chatId;
	private long memberId;
	
	public ChatMember(UUID chatId, long memberId) {
		this.chatId = chatId;
		this.memberId = memberId;
	}
	
	public UUID getChatId() { 
		return this.chatId;
	}
	
	public long getMemberId() {
		return this.memberId;
	}
	
	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("chatid", chatId.toString());
		map.put("memberid", memberId);
		return map;
	}
	
}
