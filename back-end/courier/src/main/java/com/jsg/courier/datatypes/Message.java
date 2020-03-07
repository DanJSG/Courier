package com.jsg.courier.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	
	@JsonProperty("messageText")
	private String messageText;
	
	@JsonProperty("timestamp")
	private String timestamp;
	
	@JsonProperty("sender")
	private String sender;
	
	@JsonProperty("receiver")
	private String receiver;
	
	public Message() {};
	
	public void print() {
		System.out.println("Message: " + this.messageText);
		System.out.println("Time: " + this.timestamp);
		System.out.println("Sender: " + this.sender);
		System.out.println("receiver: " + this.receiver);
	}
}
