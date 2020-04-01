package com.jsg.courier.datatypes;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private long id;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {		
		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");
		this.id = Long.parseLong(headers[0]);
	}

	public long getId() {
		return this.id;
	}
	
}
