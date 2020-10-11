package com.jsg.postie.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.postie.libs.nosql.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v1")
public abstract class Version1Controller {

	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	protected static final ResponseEntity<String> BAD_REQUEST_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	protected static final ResponseEntity<String> INTERNAL_SERVER_ERROR_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	protected static final ResponseEntity<String> NO_CONTENT_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	protected static final ResponseEntity<String> EMPTY_OK_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.OK).body(null);
	protected static final ResponseEntity<String> METHOD_NOT_ALLOWED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(null);
	protected static final ResponseEntity<String> NOT_FOUND_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

	protected static <T extends JsonObject> String mapListToJson(List<T> list) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}