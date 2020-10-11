package com.jsg.hive.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class MongoDateDeserializer extends JsonDeserializer<Date> {
	
    private static final SimpleDateFormat jsDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	
	@Override
	public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.readValueAsTree();
		if(node.toString().contains("$numberLong")) {
			return new Date(Long.parseLong(node.get("$numberLong").asText()));
		}
		
		try {
			return jsDateFormatter.parse(node.asText());
		} catch (ParseException e) {
			// if an error is thrown return date time at message arrival 
			// on server rather than client side date time
			e.printStackTrace();
			return new Date();
		}
	}
}
