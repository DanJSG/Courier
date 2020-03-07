package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	
	@JsonProperty
	private String messageText;
	
	@JsonProperty
	private String timestamp;
	
	@JsonProperty
	private String sender;
	
	@JsonProperty
	private String receiver;
	
	public Message() {};
	
	public void print() {
		System.out.println("Message: " + this.messageText);
		System.out.println("Time: " + this.timestamp);
		System.out.println("Sender: " + this.sender);
		System.out.println("receiver: " + this.receiver);
	}
}
