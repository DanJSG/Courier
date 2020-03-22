package com.jsg.courier.datatypes;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private long id;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {
		this.id = Long.parseLong(session.getHandshakeHeaders().getFirst("sec-websocket-protocol"));
//		String[] headers = session.getHandshakeHeaders().getFirst("sec-websocket-protocol").split(",");
//		this.id = Long.parseLong(headers[0].trim());
	}

	public long getId() {
		return this.id;
	}
	
}
