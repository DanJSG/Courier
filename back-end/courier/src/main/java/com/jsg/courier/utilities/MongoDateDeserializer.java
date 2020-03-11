package com.jsg.courier.utilities;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MongoDateDeserializer extends JsonDeserializer<Date> {
	
    private static final SimpleDateFormat mongoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final SimpleDateFormat jsDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	
	@Override
	public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.readValueAsTree();
		System.out.println("Json node is:");
		System.out.println(node);
		if(node.toString().contains("$numberLong")) {
			System.out.println(node.get("$numberLong").asText());
			System.out.println(new Date(Long.parseLong(node.get("$numberLong").asText())));
			return new Date(Long.parseLong(node.get("$numberLong").asText()));
		}
		System.out.println(node.asText());
		System.out.println(node.asInt());
		try {
			return jsDateFormatter.parse(node.asText());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
//		if(!node.asText().contains("$date")) {
//			try {
//				return jsDateFormatter.parse(node.asText());
//			} catch (ParseException e) {
//				e.printStackTrace();
//				return null;
//			}
//		} else {
//			try {
//				System.out.println(node.asText());
//				return mongoDateFormatter.parse(node.get("$date").asText());
//			} catch (ParseException e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
//		try {
//			return dateFormatter.parse(node.get("timestamp").asText());
//		} catch(ParseException e){
//			return null;
//		}
	}
}
