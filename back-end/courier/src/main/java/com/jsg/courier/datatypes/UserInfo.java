package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfo {

	@JsonProperty
	private long id;
	
	@JsonProperty
	private String displayName;
	
	@JsonProperty
	private String bio;
	
	public UserInfo() {}
	
	public UserInfo(long id) {
		this.id = id;
		this.displayName = "";
		this.bio = "";
	}
	
	public UserInfo(long id, String displayName, String bio) {
		this.id = id;
		this.displayName = displayName;
		this.bio = bio;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getBio() {
		return this.bio;
	}
	
}
