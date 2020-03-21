package com.jsg.courier.datatypes;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jsg.courier.repositories.UserInfoRepository;
import com.jsg.courier.utilities.MongoDateDeserializer;

public class Message {
	
	@JsonProperty
	private String messageText;
	
	@JsonProperty
	@JsonDeserialize(using = MongoDateDeserializer.class)
	private Date timestamp;
	
	@JsonProperty
	private UUID sessionId;
	
	@JsonProperty
	private long senderId;
	
	@JsonProperty
	private String sender;
	
	@JsonProperty
	private String receiver;
	
	public Message() {
//		try {
//			findSenderName();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
	}
	
	public void print() {
		System.out.println("{");
		System.out.println("messageText: " + this.messageText);
		System.out.println("timestamp: " + this.timestamp);
		System.out.println("sender: " + this.senderId);
		System.out.println("receiver: " + this.receiver);
		System.out.println("sessionId: " + this.sessionId);
		System.out.println("}");
	}
	
	public UUID getSessionId() {
		return sessionId;
	}
	
	private void findSenderName() throws Exception {
		UserInfoRepository repo = new UserInfoRepository();
		List<UserInfo> infoList = repo.findWhereEqual("id", this.senderId, 1);
		if(infoList == null || infoList.size() < 1) {
			this.sender = "Anonymous";
			return;
		}
		this.sender = infoList.get(0).getDisplayName();
	}
}
