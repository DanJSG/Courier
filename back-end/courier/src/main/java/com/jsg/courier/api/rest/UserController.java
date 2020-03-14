package com.jsg.courier.api.rest;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.courier.datatypes.User;
import com.jsg.courier.repositories.UserRepositoryOld;

@RestController
@RequestMapping("/api/account")
public class UserController {
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String createAccount(@RequestBody Map<String, String> email) throws Exception {
		System.out.println(email.get("email"));
		User user = new User(email.get("email"));
		UserRepositoryOld repo = new UserRepositoryOld();
		String response = repo.save(user);
		System.out.println(user.getId());
		return response;
	}
	
}
