package com.jsg.chatterbox.types;

import java.sql.ResultSet;
import java.util.UUID;

import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntityBuilder;

public class EmptyChatBuilder implements SQLEntityBuilder<EmptyChat>{

	@Override
	public EmptyChat fromResultSet(ResultSet sqlResults) {
		try {
			UUID id = UUID.fromString(sqlResults.getString(SQLColumn.CHAT_ID.toString()));
			String name = sqlResults.getString(SQLColumn.NAME.toString());
			return new EmptyChat(id, name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
