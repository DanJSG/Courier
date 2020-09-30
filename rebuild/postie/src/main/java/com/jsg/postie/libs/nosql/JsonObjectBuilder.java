package com.jsg.postie.libs.nosql;

public interface JsonObjectBuilder<T extends JsonObject> {
	
	public T fromJson(String json);
	
}
