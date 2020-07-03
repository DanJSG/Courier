package com.jsg.courier.datatypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.libs.nosql.JsonObjectBuilder;

public class MessageBuilder implements JsonObjectBuilder<Message> {

	@Override
	public Message fromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message message = mapper.readValue(json, Message.class);
			return message;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
