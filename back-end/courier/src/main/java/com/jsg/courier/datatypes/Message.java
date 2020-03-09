package com.jsg.courier.datatypes;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document("messages")
public class Message {
	
	@JsonProperty
	private String messageText;
	
	@JsonProperty
	private String timestamp;
	
	@JsonProperty("id")
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
	
	public int getId() {
		return sessionId;
	}
}
