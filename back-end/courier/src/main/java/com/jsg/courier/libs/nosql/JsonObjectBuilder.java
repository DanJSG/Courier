package com.jsg.courier.libs.nosql;

public interface JsonObjectBuilder<T extends JsonObject> {
	
	public T fromJson(String json);
	
}
