package com.jsg.courier.repositories;

import java.util.List;

public interface CustomRepository<T> {
	
	public void createCollection(String name);
	
	public void save(T item, String collectionName) throws Exception;
	
	public List<T> findAll(String collectionName) throws Exception;
	
	public List<T> findAll(String collectionName, int limit) throws Exception;
	
	public List<T> findAllWhere(String collectionName) throws Exception;
	
	public int count(String collectionName);
	
	public void delete(T item, String collectionName) throws Exception;
	
	public void delete(String id, String collectionName) throws Exception;
	
	public void deleteAll(String collectionName);
	
	public Boolean exists(T item, String collectionName);
	
}
