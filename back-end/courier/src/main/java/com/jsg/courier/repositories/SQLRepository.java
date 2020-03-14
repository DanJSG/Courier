package com.jsg.courier.repositories;

import java.util.List;

public interface SQLRepository<T> {

	public Boolean save(T item) throws Exception;
	
	public <V> List<T> findWhereEqual(String searchColumn, V value);
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit);
	public <V> List<T> findWhereEqual(String searchColumn, V value, String resultColumn);
	public <V> List<T> findWhereEqual(String searchColumn, V value, String resultColumn, int limit);

}
