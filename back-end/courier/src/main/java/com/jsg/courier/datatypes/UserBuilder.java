package com.jsg.courier.datatypes;

import java.sql.ResultSet;

import com.jsg.courier.libs.sql.SQLEntityBuilder;

public class UserBuilder implements SQLEntityBuilder<User> {

	@Override
	public User fromResultSet(ResultSet sqlResults) {
		try {
			long id = sqlResults.getLong("id");
			long oauthId = sqlResults.getLong("oauthid");
			String username = sqlResults.getString("username");
			return new User(id, oauthId, username);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
