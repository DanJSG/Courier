package com.jsg.courier.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.jsg.courier.datatypes.User;

public class UserRepository extends MySQLRepository implements SQLRepository<User> {
	
	public UserRepository() throws Exception {
		this.tableName = "users.accounts";
		super.openConnection();
	}
	
	@Override
	public Boolean save(User item) {
		try {
			super.save("email", "\"" + item.getEmail() + "\"");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is already an email address in the database which matches: " + item.getEmail() + ".");
			return false;
		}
	}

	@Override
	public <V> List<User> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<User> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<User> users = new ArrayList<User>();
			while(results.next()) {
				users.add(new User(results.getString("email"), results.getLong("id")));
			}
			if(users.size() == 0) {
				throw new Error();
			}
			return users;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Boolean closeConnection() throws Exception {
		return super.closeConnection();
	}
	
}