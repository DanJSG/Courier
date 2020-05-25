package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.httprequests.HttpResponse;
import com.jsg.courier.repositories.UserInfoAPIRepository;

public class UserSession {
	
	@JsonProperty
	private String displayName;
	
	@JsonProperty
	private long id;
	
	public UserSession() {}
	
	public UserSession(long id) {
		this.id = id;
		try {
			getDisplayName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getDisplayName() throws Exception {
		HttpResponse response = UserInfoAPIRepository.getUserInfo(id);
		if(response == null || response.getStatus() > 299 || response.getBody() == null) {
			throw new Exception();
		}
		UserInfo info = new ObjectMapper().readValue(response.getBody(), UserInfo.class);
		this.displayName = info.getDisplayName();
	}
	
}
