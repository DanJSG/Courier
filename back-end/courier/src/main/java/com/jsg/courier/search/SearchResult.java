package com.jsg.courier.search;

public interface SearchResult<T> extends Comparable<SearchResult<T>>{

	public void incrementHits();
	
	public void update(T result);
	
	public long checkAge();
	
	public T get();
	
	public int getHits();
	
	public String getQuery();
	
}
