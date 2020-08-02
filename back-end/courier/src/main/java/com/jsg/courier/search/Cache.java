package com.jsg.courier.search;

public interface Cache<T> {

	public void add(String key, T value);
	
	public void clear();
	
	public boolean contains(String key);
	
	public boolean changeCapacity(int newCapacity);
	
	public boolean remove(String key);
	
}
