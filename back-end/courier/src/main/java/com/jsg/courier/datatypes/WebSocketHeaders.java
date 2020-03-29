package com.jsg.courier.datatypes;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private long id;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {
		this.id = Long.parseLong(session.getHandshakeHeaders().getFirst("sec-websocket-protocol"));
	}

	public long getId() {
		return this.id;
	}
	
}
