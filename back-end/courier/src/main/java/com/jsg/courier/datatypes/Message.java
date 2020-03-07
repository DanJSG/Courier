package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	
	@JsonProperty
	private String messageText;
	
	@JsonProperty
	private String timestamp;
	
	@JsonProperty
	private String id;
	
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
		System.out.println("sessionId: " + this.id);
		System.out.println("}");
	}
	
	public String getId() {
		return id;
	}
}
