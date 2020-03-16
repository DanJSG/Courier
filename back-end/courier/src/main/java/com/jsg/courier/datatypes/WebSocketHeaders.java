package com.jsg.courier.datatypes;

import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private String username;
	
	@JsonProperty
	private UUID sessionId;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {
		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");
		this.sessionId = UUID.fromString(headers[0]);
		this.username = headers[1];
	}
	
	public UUID getSessionId() {
		return this.sessionId;
	}

	public String getUsername() {
		return this.username;
	}
	
}
