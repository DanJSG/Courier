package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String email;
	
	@JsonProperty
	private String password;
	
	@JsonProperty
	private String salt;
	
	public User() {}
	
	public User(String email, String password, String salt) {
		this.email = email;
		this.password = password;
		this.salt = salt;
	}
	
	public User(String email, String password, String salt, long id) {
		this.email = email;
		this.password = password;
		this.salt = salt;
		this.id = id;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getSalt() {
		return this.salt;
	}
	
}
