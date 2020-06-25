package com.jsg.courier.libs.sql;

import java.util.List;

public interface SQLRepository<T extends SQLEntity> {
	
	public Boolean save(T item);
	
	/**
	 * NEVER PASS COLUMN NAME VALUES FROM THE CLIENT. THIS CREATES A SQLi VULNERABILITY.
	 * @param <V>
	 * @param searchColumn
	 * @param value
	 * @return
	 */
	public <V> List<T> findWhereEqual(String searchColumn, V value, SQLEntityBuilder<T> builder);
	
	/**
	 * NEVER PASS COLUMN NAME VALUES FROM THE CLIENT. THIS CREATES A SQLi VULNERABILITY.
	 * @param <V>
	 * @param searchColumn
	 * @param value
	 * @param limit
	 * @return
	 */
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	/**
	 * NEVER PASS COLUMN NAME VALUES FROM THE CLIENT. THIS CREATES A SQLi VULNERABILITY.
	 * @param <V>
	 * @param <U>
	 * @param clauseColumn
	 * @param clauseValue
	 * @param updateColumn
	 * @param updateValue
	 * @throws Exception
	 */
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue);
	
	public Boolean closeConnection();
	
	public Boolean openConnection();
	
}
