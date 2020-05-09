package com.jsg.courier_old.datatypes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.courier_old.repositories.UserInfoRepository;

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
		UserInfoRepository repo = new UserInfoRepository();
		List<UserInfo> infoList = repo.findWhereEqual("id", this.id, 1);
		if(infoList == null || infoList.size() < 1) {
			this.displayName = "Anonymous";
			return;
		}
		this.displayName = infoList.get(0).getDisplayName();
	}
	
}
