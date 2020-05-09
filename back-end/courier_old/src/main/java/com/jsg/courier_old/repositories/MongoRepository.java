package com.jsg.courier_old.repositories;

import java.util.List;

public interface MongoRepository<T> {
	
	public void createCollection(String name);
	
	public void save(T item, String collectionName) throws Exception;
	
	public List<T> findAll(String collectionName) throws Exception;
	
	public List<T> findAll(String collectionName, int limit) throws Exception;
	
	public <V> List<T> findAllWhereEquals(String field, V value, String collectionName) throws Exception;
	
	public long count(String collectionName);
	
	public void delete(T item, String collectionName) throws Exception;
	
	public void delete(String id, String collectionName) throws Exception;
	
	public void deleteAll(String collectionName);
	
	public Boolean exists(T item, String collectionName) throws Exception;
	
	public Boolean exists(String id, String collectionName) throws Exception;
	
	public void closeConnection() throws Exception;
	
}
