package com.jsg.courier.datatypes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.courier.repositories.UserInfoRepository;

public class UserSession {
	
	@JsonProperty
	private String displayName;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	@JsonInclude(Include.NON_NULL)
	private String token;
	
	public UserSession() {}
	
	public UserSession(long id, String token) {
		this.id = id;
		this.token = token;
		try {
			getDisplayName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserSession(long id) {
		this(id, null);
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
