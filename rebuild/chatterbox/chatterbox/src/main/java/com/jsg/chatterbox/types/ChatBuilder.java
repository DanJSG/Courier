package com.jsg.chatterbox.types;

import java.sql.ResultSet;
import java.util.UUID;

import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntityBuilder;

public class ChatBuilder implements SQLEntityBuilder<Chat>{

	@Override
	public Chat fromResultSet(ResultSet sqlResults) {
		try {
			UUID id = UUID.fromString(sqlResults.getString(SQLColumn.ID.toString()));
			String name = sqlResults.getString(SQLColumn.NAME.toString());
			return new Chat(id, name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
