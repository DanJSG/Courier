package com.jsg.courier_old.api.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.bson.internal.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier_old.datatypes.User;
import com.jsg.courier_old.datatypes.UserInfo;
import com.jsg.courier_old.datatypes.UserSession;
import com.jsg.courier_old.repositories.UserInfoRepository;
import com.jsg.courier_old.repositories.UserRepository;
import com.jsg.courier_old.utilities.AuthHeaderHandler;
import com.jsg.courier_old.utilities.JWTHandler;

@RestController
@CrossOrigin(origins = "http://local.courier.net:3000")
@RequestMapping("/api/account")
public class UserController {
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> createAccount(@RequestBody Map<String, String> userDetails) throws Exception {
		User user = createUser(userDetails);
		if(user == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create new user.");
		}
		UserInfo userInfo = createUserInfo(user.getId(), userDetails.get("displayName"));
		if(userInfo == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user info.");
		}
		user.clearPassword();
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(user));
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> login(@RequestBody Map<String, String> userLogin, HttpServletResponse response) throws Exception {
		UserRepository userRepo = new UserRepository();
		List<User> results = userRepo.findWhereEqual("email", userLogin.get("email"), 1);
		if(results == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to login. Email address or password incorrect.");
		}
		User user = results.get(0);
		if(!BCrypt.checkpw(new String(Base64.decode(userLogin.get("password"))), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to login. Email address or password incorrect.");
		}
		user.clearPassword();
		String jwt = JWTHandler.createToken(user.getId());
		String xsrfJwt = JWTHandler.createToken(user.getId());
		response.addCookie(createAuthCookie("jwt", jwt));
		UserSession session = new UserSession(user.getId());
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfJwt).body(new ObjectMapper().writeValueAsString(session));
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true")
	@PostMapping(value = "/findUserInfoById")
	public @ResponseBody ResponseEntity<String> findUserInfoById(@RequestParam long searchId, @RequestParam long id, 
			@CookieValue(required = false) String jwt, @RequestHeader String authorization) throws Exception {
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(jwt, id) || !JWTHandler.tokenIsValid(headerJwt, id)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized.");
		}
		UserInfoRepository repo = new UserInfoRepository();
		List<UserInfo> userInfoList = repo.findWhereEqual("id", id, 1);
		if(userInfoList == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to find user with requested ID");
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(userInfoList.get(0)));
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true")
	@PostMapping(value = "/verifyJwt")
	public @ResponseBody ResponseEntity<Boolean> verifyJwt(@RequestParam Long id, @CookieValue(required = false) String jwt, 
			@RequestHeader String authorization) {
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(jwt, id) || !JWTHandler.tokenIsValid(headerJwt, id)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	private User createUser(Map<String, String> userDetails) throws Exception {
		String hashedPassword = BCrypt.hashpw(userDetails.get("password"), BCrypt.gensalt());
		User user = new User(userDetails.get("email"), hashedPassword);
		UserRepository userRepo = new UserRepository();
		if(!userRepo.save(user)) {
			userRepo.closeConnection();
			return null;
		}
		user = userRepo.findWhereEqual("email", user.getEmail(), 1).get(0);
		userRepo.closeConnection();
		return user;
	}
	
	private UserInfo createUserInfo(long id, String name) throws Exception {
		UserInfoRepository userInfoRepo = new UserInfoRepository();
		UserInfo userInfo = new UserInfo(id, name);
		if(!userInfoRepo.save(userInfo)) {
			userInfoRepo.closeConnection();
			return null;
		}
		userInfoRepo.closeConnection();
		return userInfo;
	}
	
	private Cookie createAuthCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(900);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
}
