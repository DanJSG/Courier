package com.jsg.courier.repositories;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Properties;

import com.jsg.courier.datatypes.JSONResponse;
import com.jsg.courier.datatypes.User;

public class UserRepository {
	
	Connection connection;
	
	public UserRepository() throws Exception {
		Properties properties = new Properties();
		properties.put("user", "localDev");
		properties.put("password", "l0c4l_d3v!");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/courier", properties);
	}
	
	public String save(User user) throws Exception {
		Statement statement = connection.createStatement();
		String queryTemplate = "INSERT INTO `users.accounts` (email) VALUES (\"%s\");";
		String query = String.format(queryTemplate, user.getEmail());
//		String query = "INSERT INTO `users.accounts` (email) VALUES (\"" + user.getEmail() + "\")";
		try {
			statement.execute(query);
		} catch (SQLIntegrityConstraintViolationException e) {
			// TODO determine own response code to include here
			return new JSONResponse(e.getErrorCode(),
					"Could not create new user. An account with that email address already exists.").toString();
		}
		user.setId(((BigInteger) find("id", user.getEmail())).longValue());
		return new JSONResponse(200, "Created new user with email address " + user.getEmail() + ".").toString();
	}
	
	// TODO FIX THIS MESS OF A FUNCTION --> THE GENERICS ARE ALL WRONG AND ITS NOT A GENERAL PURPOSE FUNCTION
	public <V> Object find(String columnName, V value) throws Exception {
		Statement statement = connection.createStatement();
		String queryTemplate = "SELECT id FROM `users.accounts` WHERE email=\"%s\" LIMIT 1;";
		String query = String.format(queryTemplate, value);
		ResultSet results = statement.executeQuery(query);
		while(results.next()) {
			return results.getObject(columnName);
		}
		return -1;
	}
	
}
