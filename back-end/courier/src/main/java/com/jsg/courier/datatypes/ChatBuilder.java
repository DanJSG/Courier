package com.jsg.courier.datatypes;

import java.sql.ResultSet;
import java.util.UUID;

import com.jsg.courier.libs.sql.SQLEntityBuilder;

public class ChatBuilder implements SQLEntityBuilder<Chat> {

	@Override
	public Chat fromResultSet(ResultSet sqlResults) {
		try {
			UUID id = UUID.fromString(sqlResults.getString("chatid"));
			String name = sqlResults.getString("name");
			return new Chat(id, name);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
