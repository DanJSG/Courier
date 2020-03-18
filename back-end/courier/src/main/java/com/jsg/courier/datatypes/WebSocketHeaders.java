package com.jsg.courier.datatypes;

import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private UUID sessionId;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {
		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");
		this.sessionId = UUID.fromString(headers[0]);
		this.id = Long.parseLong(headers[1].trim());
	}
	
	public UUID getSessionId() {
		return this.sessionId;
	}

	public long getUsername() {
		return this.id;
	}
	
}
