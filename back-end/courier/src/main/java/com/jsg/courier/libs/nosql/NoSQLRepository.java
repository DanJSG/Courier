package com.jsg.courier.libs.nosql;

import java.util.List;

public interface NoSQLRepository<T extends JsonObject> {
	
	public void createCollection(String name);
	
	public void save(T item, String collectionName) throws Exception;
	
	public List<T> findAll(String collectionName, JsonObjectBuilder<T> builder) throws Exception;
	
	public List<T> findAll(String collectionName, int limit, JsonObjectBuilder<T> builder) throws Exception;
	
	public <V> List<T> findAllWhereEquals(String field, V value, String collectionName, JsonObjectBuilder<T> builder) throws Exception;
	
	public long count(String collectionName);
	
	public void delete(T item, String collectionName) throws Exception;
	
	public void delete(String id, String collectionName) throws Exception;
	
	public void deleteAll(String collectionName);
	
	public Boolean exists(T item, String collectionName) throws Exception;
	
	public Boolean exists(String id, String collectionName) throws Exception;
	
}
