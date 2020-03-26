package com.jsg.courier.datatypes;

import org.springframework.web.socket.WebSocketSession;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketHeaders {
	
	@JsonProperty
	private long id;

	public WebSocketHeaders() {}
	
	public WebSocketHeaders(WebSocketSession session) {
		String token = session.getHandshakeHeaders().getFirst("sec-websocket-protocol");
		this.id = JWT.decode(token).getClaim("id").asLong();
	}

	public long getId() {
		return this.id;
	}
	
}
