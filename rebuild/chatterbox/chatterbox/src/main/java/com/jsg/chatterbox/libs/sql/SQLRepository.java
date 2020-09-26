package com.jsg.chatterbox.libs.sql;

import java.util.List;
import java.util.Map;

public interface SQLRepository<T extends SQLEntity> {
	
	Boolean save(T item);
	
	Boolean saveMany(List<T> objects);
	
	<V> List<T> findWhereEqual(SQLColumn searchColumn, V value, SQLEntityBuilder<T> builder);
	
	<V> List<T> findWhereEqual(SQLColumn searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	<V> List<T> findWhereEqual(List<SQLColumn> searchColumns, List<V> values, int limit, SQLEntityBuilder<T> builder);
	
	<V> List<T> findWhereEqual(List<SQLColumn> searchColumns, List<V> values, SQLEntityBuilder<T> builder);
	
	<V> List<T> findWhereLike(SQLColumn searchColumn, V value, SQLEntityBuilder<T> builder);
	
	<V> List<T> findWhereLike(SQLColumn searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	<V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, Map<SQLColumn, U> row);
	
	<V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, SQLColumn updateColumn, U updateValue);
	
	<V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, List<SQLColumn> updateColumns, List<U> updateValues);
	
	<V> Boolean deleteWhereEquals(SQLColumn clauseColumn, V clauseValue);

	<V, U> Boolean deleteWhereEquals(Map<SQLColumn, U> conditionMap);

}
