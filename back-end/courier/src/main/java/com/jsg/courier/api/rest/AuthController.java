package com.jsg.courier.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.datatypes.AuthToken;
import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.libs.sql.MySQLRepository;
import com.jsg.courier.libs.sql.SQLTable;

@RestController
public class AuthController extends APIController {
	
	@Autowired
	public AuthController(
			@Value("${TOKEN_ACCESS_SECRET}") String accessTokenSecret,
			@Value("${OAUTH2_CLIENT_ID}") String client_id, 
			@Value("${OAUTH2_CLIENT_SECRET}") String client_secret) {
		super(accessTokenSecret, client_id, client_secret);
	}
	
	@PostMapping(value = "/authorize")
	public @ResponseBody ResponseEntity<String> authorize(@RequestHeader AuthToken authorization) throws Exception {
		MySQLRepository<User> repo = new MySQLRepository<>(SQLTable.USERS);
		long id = authorization.getId();
		String name = authorization.getName();
		List<User> foundUsers = repo.findWhereEqual("id", id, new UserBuilder());
		if(foundUsers == null || foundUsers.size() == 0) {
			repo.save(new User(id, name));
		}
		foundUsers = repo.findWhereEqual("id", id, new UserBuilder());
		if(foundUsers == null || foundUsers.size() == 0) {
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		User user = foundUsers.get(0);
		return ResponseEntity.status(HttpStatus.OK).body(user.writeValueAsString());
	}

}
