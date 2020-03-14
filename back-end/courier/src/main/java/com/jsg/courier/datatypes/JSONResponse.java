package com.jsg.courier.datatypes;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONResponse {
	
	private Map<String,Object> responseMap;
	private String response;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public JSONResponse(Integer code, String message) throws Exception {
		this.responseMap = new HashMap<String, Object>();
		this.responseMap.put("message", message);
		this.responseMap.put("code", code);
		response = objectMapper.writeValueAsString(responseMap);
	}
	
	@Override
	public String toString() {
		return response;
	}
	
}
