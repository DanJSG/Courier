package com.jsg.postie.libs.nosql;

import java.util.List;

public interface NoSQLRepository<T extends JsonObject> {
	
	void createCollection(String name);
	
	void save(T item, String collectionName);
	
	List<T> findAll(String collectionName, JsonObjectBuilder<T> builder);
	
	List<T> findAll(String collectionName, int limit, JsonObjectBuilder<T> builder);
	
	<V> List<T> findAllWhereEquals(String field, V value, String collectionName, JsonObjectBuilder<T> builder);
	
	long count(String collectionName);
	
	void delete(T item, String collectionName);
	
	void delete(String id, String collectionName);
	
	void deleteAll(String collectionName);
	
	Boolean exists(T item, String collectionName);
	
	Boolean exists(String id, String collectionName);
	
}
