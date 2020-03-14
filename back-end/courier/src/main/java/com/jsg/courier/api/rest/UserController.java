package com.jsg.courier.api.rest;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.courier.datatypes.JSONResponse;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.repositories.UserRepository;

@RestController
@RequestMapping("/api/account")
public class UserController {
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String createAccount(@RequestBody Map<String, String> email) throws Exception {
		User user = new User(email.get("email"));
		UserRepository repo = new UserRepository();
		if(!repo.save(user)) {
			repo.closeConnection();
			return new JSONResponse(500, "Failed to create user. An account with this email address already exists.")
					.toString();
		}
		user = repo.findWhereEqual("email", "\"" + user.getEmail() + "\"", 1).get(0);
		repo.closeConnection();
		return new JSONResponse(200, 
				"Successfully created new user with email address: " + user.getEmail() + " and User ID: " + user.getId() + ".")
				.toString();
	}
	
}
