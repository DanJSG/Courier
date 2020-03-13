package com.jsg.courier.datatypes;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private String username;
	
	@JsonProperty
	private int sessionId;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {
		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");
		this.sessionId = Integer.parseInt(headers[0]);
		this.username = headers[1];
	}
	
	public int getSessionId() {
		return this.sessionId;
	}

	public String getUsername() {
		return this.username;
	}
	
}
