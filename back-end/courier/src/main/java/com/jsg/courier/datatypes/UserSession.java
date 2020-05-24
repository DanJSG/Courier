package com.jsg.courier.datatypes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.courier.repositories.UserInfoAPIRepository;
import com.jsg.courier.repositories.UserInfoRepository;

public class UserSession {
	
	@JsonProperty
	private String displayName;
	
	@JsonProperty
	private long id;
	
	public UserSession() {}
	
	public UserSession(long id, String connectionString, String username, String password) {
		this.id = id;
		try {
			getDisplayName(connectionString, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getDisplayName(String connectionString, String username, String password) throws Exception {
		UserInfoRepository repo = new UserInfoRepository(connectionString, username, password);
		List<UserInfo> infoList = repo.findWhereEqual("id", this.id, 1);
		if(infoList == null || infoList.size() < 1) {
			this.displayName = "Anonymous";
			return;
		}
		this.displayName = infoList.get(0).getDisplayName();
	}
	
}
