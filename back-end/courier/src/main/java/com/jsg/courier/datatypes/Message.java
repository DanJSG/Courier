package com.jsg.courier.datatypes;

public class Message {
	
	private String messageText;
	private String timestamp;
	private String sender;
	private String receiver;
	
	public Message(String messageText, String timestamp, String sender, String receiver) {
		this.messageText = messageText;
		this.timestamp = timestamp;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public void print() {
		System.out.println("Message: " + this.messageText);
		System.out.println("Time: " + this.timestamp);
		System.out.println("Sender: " + this.sender);
		System.out.println("receiver: " + this.receiver);
	}
	
}
