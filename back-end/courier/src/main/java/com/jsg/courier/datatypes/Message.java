package com.jsg.courier.datatypes;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jsg.courier.libs.nosql.JsonObject;
import com.jsg.courier.utilities.MongoDateDeserializer;

public class Message implements JsonObject {
	
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
	
	@JsonProperty
	@JsonInclude(Include.NON_NULL)
	private UUID chatId;
	
	public Message() {}
	
	public UUID getChatId() {
		return this.chatId;
	}
	
	public void print() {
		System.out.println("{");
		System.out.println("messageText: " + this.messageText);
		System.out.println("timestamp: " + this.timestamp);
		System.out.println("sender ID: " + this.senderId);
		System.out.println("sender: " + this.sender);
		System.out.println("receiver: " + this.receiver);
		System.out.println("}");
	}

	@Override
	public String writeValueAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(this);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
