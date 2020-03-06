package com.jsg.courier.api;

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
	
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/send")
	public void sendMessage(@RequestBody Message message, @RequestHeader String token) {
		System.out.println("Message received!");
		System.out.println(token);
		message.print();
	}
	
}
