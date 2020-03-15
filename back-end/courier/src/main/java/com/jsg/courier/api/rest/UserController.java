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
import com.jsg.courier.datatypes.UserInfo;
import com.jsg.courier.repositories.UserInfoRepository;
import com.jsg.courier.repositories.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

@RestController
@RequestMapping("/api/account")
public class UserController {
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String createAccount(@RequestBody Map<String, String> userDetails) throws Exception {
		User user = createUser(userDetails);
		if(user == null) {
			return new JSONResponse(500, "Failed to create user. An account with this email address already exists.").toString();
		}
		UserInfo userInfo = createUserInfo(user);
		if(userInfo == null) {
			return new JSONResponse(500, "Failed to create user. Duplicate user ID in user information table.")
			.toString();
		}
		System.out.println(user.getEmail());
		System.out.println(user.getId());
		System.out.println(user.getPassword());
		System.out.println(user.getSalt());
		System.out.println(userInfo.getDisplayName());
		System.out.println(userInfo.getBio());
		System.out.println(userInfo.getId());
		return new JSONResponse(200, 
				"Successfully created new user with email address: " + user.getEmail() + " and User ID: " + user.getId() + ".")
				.toString();
	}
	
	private User createUser(Map<String, String> userDetails) throws Exception {
		String salt = BCrypt.gensalt();
		String hashedPassword = BCrypt.hashpw(userDetails.get("password"), salt);
		User user = new User(userDetails.get("email"), hashedPassword, salt);
		UserRepository userRepo = new UserRepository();
		if(!userRepo.save(user)) {
			userRepo.closeConnection();
			return null;
		}
		user = userRepo.findWhereEqual("email", user.getEmail(), 1).get(0);
		userRepo.closeConnection();
		return user;
	}
	
	private UserInfo createUserInfo(User user) throws Exception {
		UserInfoRepository userInfoRepo = new UserInfoRepository();
		UserInfo userInfo = new UserInfo(user.getId());
		if(!userInfoRepo.save(userInfo)) {
			userInfoRepo.closeConnection();
			return null;
		}
		userInfoRepo.closeConnection();
		return userInfo;
	}
	
}
