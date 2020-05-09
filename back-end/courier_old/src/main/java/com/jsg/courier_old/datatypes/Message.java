package com.jsg.courier_old.datatypes;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jsg.courier_old.utilities.MongoDateDeserializer;

public class Message {
	
	@JsonProperty
	private String messageText;
	
	@JsonProperty
	@JsonDeserialize(using = MongoDateDeserializer.class)
	private Date timestamp;
	
	@JsonProperty
	private long senderId;
	
	@JsonProperty
	private String sender;
	
	@JsonProperty
	private String receiver;
	
	public Message() {}
	
	public void print() {
		System.out.println("{");
		System.out.println("messageText: " + this.messageText);
		System.out.println("timestamp: " + this.timestamp);
		System.out.println("sender ID: " + this.senderId);
		System.out.println("sender: " + this.sender);
		System.out.println("receiver: " + this.receiver);
		System.out.println("}");
	}
	
}
