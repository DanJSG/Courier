package com.jsg.courier.search;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class SearchCache<T extends SearchResult<V>, V> implements Cache<T> {
	
	private Map<String, T> cache;
	private BlockingQueue<T> cacheHeap;
	private int capacity;
	private int size;
	
	public SearchCache(int capacity) {
		cache = new ConcurrentHashMap<>(capacity / 2);
		cacheHeap = new PriorityBlockingQueue<>(capacity / 2);
		this.capacity = capacity;
		size = 0;
	}
	
	public void add(String search, T result) {
		if(result == null) return;
		if(size == capacity) {
			cacheHeap.remove();
			cache.remove(search);
		}
		cacheHeap.add(result);
		cache.put(search, result);
		printInfo();
	}
	
	public void clear() {
		cache = new ConcurrentHashMap<>(capacity / 2); 
		cacheHeap = new PriorityBlockingQueue<>(capacity / 2);
		size = 0;
	}
	
	public boolean contains(String search) {
		return cache.containsKey(search);
	}
	
	public boolean changeCapacity(int newCapacity) {
		if(newCapacity > size) return false;
		capacity = newCapacity;
		return true;
	}
	
	public boolean remove(String search) {
		T value = cache.remove(search);
		if(value != null) {
			cacheHeap.remove(value);
			size--;
		}
		return value != null ? true : false;
	}

	@Override
	public T get(String key) {
		T result = cache.get(key);
		if(result != null) {
			result.incrementHits();
		}
		printInfo();
		return result;
	}
	
	private void printInfo() {
		for(T item : cacheHeap) {
			System.out.println(item.get() + ": " + item.getHits() + " hits.");
			System.out.println("Size: " + size + "; Capacity: " + capacity);
		}
	}

}
