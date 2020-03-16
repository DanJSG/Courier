package com.jsg.courier.api.rest;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.courier.datatypes.Message;

@RestController
@RequestMapping("/api")
public class MessageController {
	
	@PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:3000")
	public void sendMessage(@RequestBody Map<String, String> body) {
		System.out.println("Message received!");
		System.out.println(body.get("email"));
	}	
}
