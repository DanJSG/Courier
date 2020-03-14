package com.jsg.courier.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public abstract class MySQLRepository  {

	protected String tableName;
	private Connection connection;
	
	protected void openConnection() throws Exception {
		Properties properties = new Properties();
		properties.put("user", "localDev");
		properties.put("password", "l0c4l_d3v!");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/courier", properties);
	}
	
	protected Boolean closeConnection() throws Exception {
		if(connection.isClosed()) {
			return false;
		}
		connection.close();
		return true;
	}
	
	protected void save(String properties, String values) throws Exception {
		Statement statement = connection.createStatement();
		String query = "INSERT INTO `" + tableName + "` (" + properties + ") VALUES (" + values + ");";
		System.out.println("Sending query:");
		System.out.println(query);
		statement.execute(query);
	}
	
	protected <V> ResultSet findWhereEquals(String column, V value, String resultColumn, int limit) throws Exception {
		Statement statement = connection.createStatement();
		statement.setFetchSize(limit);
		String query = "SELECT " + resultColumn + " FROM courier.`" + tableName + "` WHERE " + column + " = " + value + ";";
		System.out.println("Sending query:");
		System.out.println(query);
		ResultSet results = statement.executeQuery(query);
		return results;
	}
	
}
