package com.jsg.courier.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
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
	
	protected void save(Map<String, Object> valueMap) throws Exception {
		String query = "INSERT INTO `" + tableName + "` (" + stringifyKeys(valueMap) + 
				") VALUES (" + stringifyValues(valueMap) + ");";
		Statement statement = connection.createStatement();
		System.out.println("Sending query:");
		System.out.println(query);
		statement.execute(query);
	}
	
	@SuppressWarnings("unchecked")
	protected <V> ResultSet findWhereEquals(String column, V value, String resultColumn, int limit) throws Exception {
		if(value.getClass() == String.class) {
			value = (V) ("\"" + value + "\"");
		}
		Statement statement = connection.createStatement();
		statement.setFetchSize(limit);
		String query = "SELECT " + resultColumn + " FROM courier.`" + tableName + "` WHERE " + column + " = " + value + ";";
		System.out.println("Sending query:");
		System.out.println(query);
		ResultSet results = statement.executeQuery(query);
		return results;
	}
	
	private String stringifyKeys(Map<String, Object> valueMap) {
		String keyString = new String();
		Object[] keys = valueMap.keySet().toArray();
		for(int i=0; i< valueMap.keySet().size(); i++) {
			keyString += keys[i];
			if(i != valueMap.keySet().size() - 1) {
				keyString += ", ";
			}
		}
		return keyString;
	}
	
	private String stringifyValues(Map<String, Object> valueMap) {
		String valueString = new String();
		Object[] values = valueMap.values().toArray();
		for(int i=0; i< valueMap.values().size(); i++) {
			if(values[i].getClass() == String.class) {
				values[i] = "\"" + values[i] + "\"";
			}
			valueString += values[i];
			if(i != valueMap.values().size() - 1) {
				valueString += ",";
			}
		}
		return valueString;
	}
	
}
