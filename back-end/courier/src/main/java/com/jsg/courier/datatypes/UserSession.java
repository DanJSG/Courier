package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.httprequests.HttpResponse;
import com.jsg.courier.repositories.UserInfoAPIRepository;

public class UserSession {
	
	@JsonProperty
	private String displayName;
	
	@JsonProperty
	private long id;
	
	@JsonCreator
	private UserSession() {}
	
	public UserSession(long id, String client_id, String client_secret) {
		this.id = id;
		try {
			getDisplayName(client_id, client_secret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getDisplayName(String client_id, String client_secret) throws Exception {
		HttpResponse response = UserInfoAPIRepository.getUserInfo(id, client_id, client_secret);
		if(response == null || response.getStatus() > 299 || response.getBody() == null) {
			this.displayName = "Deleted user";
			return;
		}
		UserInfo info = new ObjectMapper().readValue(response.getBody(), UserInfo.class);
		this.displayName = info.getDisplayName();
	}
	
}
