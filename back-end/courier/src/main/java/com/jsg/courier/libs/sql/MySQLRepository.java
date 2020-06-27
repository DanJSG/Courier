package com.jsg.courier.libs.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MySQLRepository<T extends SQLEntity> implements SQLRepository<T>{

	private Connection connection;
	
	private final String tableName;
	private final String connectionString;
	private final String sqlUsername;
	private final String sqlPassword;
	
	public MySQLRepository(String connectionString, String username, String password, String tableName) {
		this.connectionString = connectionString;
		this.sqlUsername = username;
		this.sqlPassword = password;
		this.tableName = tableName;
	}
	
	@Override
	public Boolean openConnection() {
		Properties properties = new Properties();
		properties.put("user", sqlUsername);
		properties.put("password", sqlPassword);
		try {
			connection = DriverManager.getConnection(connectionString, properties);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	@Override
	public Boolean closeConnection() {
		try {
			if(connection.isClosed()) {
				return false;
			}
			connection.close();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	@Override
	public Boolean save(T object) {
		Map<String, Object> valueMap = object.toHashMap();
		Object[] values = valueMap.values().toArray();
		String query = 
				"INSERT INTO `" + tableName + "` (" + stringifyKeys(valueMap) + 
				") VALUES (" + createParamMarkers(values) + ");";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(query);
			for(int i=0; i < values.length; i++) {
				statement.setObject(i + 1, values[i]);
			}
			statement.execute();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit, SQLEntityBuilder<T> builder) {
		String query = "SELECT * FROM `" + tableName + "` WHERE " + searchColumn + "=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setFetchSize(limit);
			statement.setObject(1, value);
			ResultSet results = statement.executeQuery();
			ArrayList<T> objectList = new ArrayList<>();
			while(results.next()) {
				objectList.add(builder.fromResultSet(results));
			}
			if(objectList.size() == 0) {
				return null;
			}
			return objectList;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <V> List<T> findWhereEqual(String searchColumn, V value, SQLEntityBuilder<T> builder) {
		return findWhereEqual(searchColumn, value, 0, builder);
	}
	
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue) {
		String query = "UPDATE `" + tableName + "` SET " + updateColumn + "= ? WHERE " + clauseColumn + " = ?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setObject(1, updateValue);
			statement.setObject(2, clauseValue);
			statement.executeUpdate();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String stringifyKeys(Map<String, Object> valueMap) {
		String keyString = new String();
		Object[] keys = valueMap.keySet().toArray();
		for(int i=0; i< valueMap.keySet().size(); i++) {
			keyString += keys[i];
			if(i != valueMap.keySet().size() - 1) {
				keyString += ",";
			}
		}
		return keyString;
	}
	
	private String createParamMarkers(Object[] values) {
		String paramString = new String();
		for(int i=0; i < values.length; i++) {
			paramString += "?";
			if(i < values.length - 1) {
				paramString += ",";
			}
		}
		return paramString;
	}
	
}
