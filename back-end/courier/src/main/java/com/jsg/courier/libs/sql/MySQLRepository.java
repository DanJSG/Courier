package com.jsg.courier.libs.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsg.courier.config.SQLConnectionPool;

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
//		Properties properties = new Properties();
//		properties.put("user", sqlUsername);
//		properties.put("password", sqlPassword);
//		try {
//			connection = DriverManager.getConnection(connectionString, properties);
//			return true;
//		} catch(Exception e) {
//			return false;
//		}
		return true;
	}
	
	@Override
	public Boolean closeConnection() {
//		try {
//			if(connection.isClosed()) {
//				return false;
//			}
//			connection.close();
//			return true;
//		} catch(Exception e) {
//			return false;
//		}
		return true;
	}
	
	@Override
	public Boolean save(T object) {
		Connection connection = getConnection();
		if(connection == null) {
			return false;
		}
		Map<String, Object> valueMap = object.toSqlMap();
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
		Connection connection = getConnection();
		if(connection == null) {
			return null;
		}
		if(!checkColumnName(searchColumn)) {
			System.out.println("Column name contains dangerous characters.");
			return null;
		}
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
		Connection connection = getConnection();
		if(connection == null) {
			return false;
		}
		if(!checkColumnName(clauseColumn) || !checkColumnName(updateColumn)) {
			System.out.println("Column name contains dangerous characters.");
			return false;
		}
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
	
	private Boolean checkColumnName(String columnName) {
		// Blocks SQL column name from including dangerous input:
		// spaces or --. This helps prevent SQL injection through a badly 
		// implemented query.
		Pattern blacklist = Pattern.compile("[ ][--]*");
		Matcher matcher = blacklist.matcher(columnName);
		return !matcher.find();
	}
	
	private Connection getConnection() {
		try {
			return SQLConnectionPool.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
