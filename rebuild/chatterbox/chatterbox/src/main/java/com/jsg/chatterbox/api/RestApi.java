package com.jsg.chatterbox.api;

import org.springframework.http.ResponseEntity;

public interface RestApi<T, V> {
	
	/**
	 * Method for getting a specific resource from the persistence layer or cache using a given parameter.
	 * 
	 * @param param		the parameter used to identify the resource to retrieve
	 * @return			a HTTP response with a string body (commonly JSON or null)
	 */
	ResponseEntity<String> get(V param);
	
	/**
	 * Method for creating and persisting or processing the resource provided.
	 * 
	 * @param body		the resource to process or persist
	 * @return			a HTTP response with a string body (commonly JSON or null)
	 */
	ResponseEntity<String> post(T body);
	
	/**
	 * Method for replacing an existing resource with the resource provided.
	 * 
	 * @param body		the resource to replace the existing resource with
	 * @return			a HTTP response with a string body (commonly JSON or null)
	 */
	ResponseEntity<String> put(T body);
	
	/**
	 * Method for deleting an existing resource from the persistence layer. 
	 * 
	 * @param param		the parameter used to identify the resource to delete
	 * @return			a HTTP response with a string body (commonly JSON or null)
	 */
	ResponseEntity<String> delete(V param);
	
}
