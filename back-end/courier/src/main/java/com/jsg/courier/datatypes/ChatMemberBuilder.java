package com.jsg.courier.datatypes;

import java.sql.ResultSet;
import java.util.UUID;

import com.jsg.courier.libs.sql.SQLEntityBuilder;

public class ChatMemberBuilder implements SQLEntityBuilder<ChatMember>{

	@Override
	public ChatMember fromResultSet(ResultSet sqlResults) {
		try {
			UUID chatid = UUID.fromString(sqlResults.getString("chatid"));
			long memberid = sqlResults.getLong("memberid");
			return new ChatMember(chatid, memberid);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
