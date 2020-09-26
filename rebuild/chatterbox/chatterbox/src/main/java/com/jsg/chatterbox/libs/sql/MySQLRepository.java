package com.jsg.chatterbox.libs.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MySQLRepository<T extends SQLEntity> implements SQLRepository<T>{
	
	private final String tableName;
	
	public MySQLRepository(SQLTable tableName) {
		this.tableName = tableName.name();
	}
	
	@Override
	public Boolean save(T object) {
		return saveMany(Arrays.asList(object));
	}
	
	@Override
	public Boolean saveMany(List<T> objects) {
		Connection connection = getConnection();
		if(connection == null || objects.size() == 0) {
			return false;
		}
		Map<SQLColumn, Object> valueMap = objects.get(0).toSqlMap();
		Object[] values = valueMap.values().toArray();
		String query = 
				"INSERT INTO `" + tableName + "` (" + stringifyKeys(valueMap) + 
				") VALUES (" + createParamMarkers(values) + ")";
		PreparedStatement statement;
		try {
			int count = 0;
			statement = connection.prepareStatement(query);
			for(T object : objects) {
				if(count > 0) {
					valueMap = object.toSqlMap();
					values = valueMap.values().toArray();
				}
				for(int i=0; i < values.length; i++) {
					statement.setObject(i + 1, values[i]);
				}
				statement.addBatch();
				count++;
				if(count % 500 == 0 || count == objects.size()) {
					statement.executeBatch();
					connection.commit();
					connection.close();
				}
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public <V> List<T> findWhereEqual(SQLColumn searchColumn, V value, int limit, SQLEntityBuilder<T> builder) {
		return findWhereEqual(Arrays.asList(searchColumn), Arrays.asList(value), limit, builder);
	}
	
	@Override
	public <V> List<T> findWhereEqual(List<SQLColumn> searchColumns, List<V> values, int limit, SQLEntityBuilder<T> builder) {
		Connection connection = getConnection();
		if(connection == null) {
			return null;
		}
		if(searchColumns.size() != values.size()) {
			return null;
		}
		String baseQuery = "SELECT * FROM `" + tableName + "` WHERE ";
		StringBuilder queryCondition = new StringBuilder();
		for(int i=0; i < searchColumns.size(); i++) {
			queryCondition.append(searchColumns.get(i).name()).append("=?");
			if(i < searchColumns.size() - 1) {
				queryCondition.append(" AND ");
			}
		}
		queryCondition.append(";");
		String query = baseQuery + queryCondition;
		try {
			return runCustomSelectQuery(connection, query, values, limit, builder);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <V> List<T> findWhereLike(SQLColumn searchColumn, V value, int limit, SQLEntityBuilder<T> builder) {
		Connection connection = getConnection();
		if(connection == null) {
			return null;
		}
		String query = "SELECT * FROM `" + tableName + "` WHERE " + searchColumn.name() + " LIKE ?;";
		try {
			return runCustomSelectQuery(connection, query, Arrays.asList(value), limit, builder);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <V> List<T> findWhereLike(SQLColumn searchColumn, V value, SQLEntityBuilder<T> builder) {
		return findWhereLike(searchColumn, value, 0, builder);
	}
	
	@Override
	public <V> List<T> findWhereEqual(SQLColumn searchColumn, V value, SQLEntityBuilder<T> builder) {
		return findWhereEqual(searchColumn, value, 0, builder);
	}
	
	@Override
	public <V> List<T> findWhereEqual(List<SQLColumn> searchColumns, List<V> values, SQLEntityBuilder<T> builder) {
		return findWhereEqual(searchColumns, values, 0, builder);
	}
	
	@Override
	public <V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, Map<SQLColumn, U> row) {
		ArrayList<SQLColumn> columns = new ArrayList<>(row.size());
		ArrayList<U> values = new ArrayList<>(row.size());
		row.forEach((key, value) -> {
			columns.add(key);
			values.add(value);
		});
		return updateWhereEquals(clauseColumn, clauseValue, columns, values);
	}
	
	@Override
	public <V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, SQLColumn updateColumn, U updateValue) {
		return updateWhereEquals(clauseColumn, clauseValue, Arrays.asList(updateColumn), Arrays.asList(updateValue));
	}
	
	@Override
	public <V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, List<SQLColumn> updateColumns, List<U> updateValues) {
		Connection connection = getConnection();
		if(connection == null) {
			return false;
		}
		StringBuilder query = new StringBuilder("UPDATE `" + tableName + "` SET ");
		if(updateColumns.size() != updateValues.size())
			return false;
		for(int i=0; i < updateColumns.size(); i++) {
			query.append(updateColumns.get(i).name()).append("=?");
			if(i < updateColumns.size() - 1)
				query.append(", ");
		}
		query.append(" WHERE ").append(clauseColumn.name()).append("=?;");
		try {
			PreparedStatement statement = connection.prepareStatement(query.toString());
			int i = 0;
			for(; i < updateColumns.size(); i++) {
				statement.setObject(i + 1, updateValues.get(i));
			}
			statement.setObject(i + 1, clauseValue);
			statement.executeUpdate();
			connection.commit();
			connection.close();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public <V> Boolean deleteWhereEquals(SQLColumn clauseColumn, V clauseValue) {
		Connection connection = getConnection();
		if(connection == null)
			return false;
		String query = "DELETE FROM `" + tableName + "` WHERE " + clauseColumn.name() + "=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setObject(1, clauseValue);
			statement.executeUpdate();
			connection.commit();
			connection.close();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String stringifyKeys(Map<SQLColumn, Object> valueMap) {
		StringBuilder keyString = new StringBuilder();
		SQLColumn[] keys = (SQLColumn[]) valueMap.keySet().toArray();
		for(int i=0; i< valueMap.keySet().size(); i++) {
			keyString.append(keys[i].name());
			if(i != valueMap.keySet().size() - 1) {
				keyString.append(",");
			}
		}
		return keyString.toString();
	}
	
	private String createParamMarkers(Object[] values) {
		StringBuilder paramString = new StringBuilder();
		for(int i=0; i < values.length; i++) {
			paramString.append("?");
			if(i < values.length - 1) {
				paramString.append(",");
			}
		}
		return paramString.toString();
	}
	
	private Connection getConnection() {
		try {
			Connection connection = SQLConnectionPool.getConnection();
			connection.setAutoCommit(false);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private <V> List<T> runCustomSelectQuery(Connection connection, String query, List<V> values, int limit, SQLEntityBuilder<T> builder) throws Exception {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setFetchSize(limit);
		for(int i=0; i < values.size(); i++) {
			statement.setObject(i + 1, values.get(i));
		}
		ResultSet results = statement.executeQuery();
		connection.commit();
		ArrayList<T> objectList = new ArrayList<>();
		while(results.next()) {
			objectList.add(builder.fromResultSet(results));
		}
		if(objectList.size() == 0) {
			connection.close();
			return null;
		}
		connection.close();
		return objectList;
	}
	
}
