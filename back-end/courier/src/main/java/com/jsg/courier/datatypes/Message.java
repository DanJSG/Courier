package com.jsg.courier.datatypes;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jsg.courier.utilities.MongoDateDeserializer;

public class Message {
	
	@JsonProperty
	private String messageText;
	
	@JsonProperty
	@JsonDeserialize(using = MongoDateDeserializer.class)
	private Date timestamp;
	
	@JsonProperty
	private int sessionId;
	
	@JsonProperty
	private String sender;
	
	@JsonProperty
	private String receiver;
	
	public Message() {};
	
	public void print() {
		System.out.println("{");
		System.out.println("messageText: " + this.messageText);
		System.out.println("timestamp: " + this.timestamp);
		System.out.println("sender: " + this.sender);
		System.out.println("receiver: " + this.receiver);
		System.out.println("sessionId: " + this.sessionId);
		System.out.println("}");
	}
	
	public int getSessionId() {
		return sessionId;
	}
}
