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
import com.jsg.courier.datatypes.User;
import com.jsg.courier.datatypes.UserBuilder;
import com.jsg.courier.libs.sql.MySQLRepository;
import com.jsg.courier.utilities.JWTHandler;

@RestController
public class AuthController extends APIController {
	
	@Autowired
	public AuthController(
			@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${oauth2.client_id}") String client_id, 
			@Value("${oauth2.client_secret}") String client_secret) {
		super(accessTokenSecret, client_id, client_secret);
	}
	
	@PostMapping(value = "/authorize")
	public @ResponseBody ResponseEntity<String> authorize(
			@CookieValue(name = OAuth2.ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization) throws Exception {
		if(!tokensAreValid(authorization, jwt)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		MySQLRepository<User> repo = new MySQLRepository<>("users");
		long oauthId = JWTHandler.getIdFromToken(jwt);
		String name = JWTHandler.getNameFromToken(jwt);
		List<User> foundUsers = repo.findWhereEqual("oauthid", oauthId, new UserBuilder());
		if(foundUsers == null || foundUsers.size() == 0) {
			repo.save(new User(oauthId, name));
		}
		foundUsers = repo.findWhereEqual("oauthid", oauthId, new UserBuilder());
		if(foundUsers == null || foundUsers.size() == 0) {
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
		User user = foundUsers.get(0);
		return ResponseEntity.status(HttpStatus.OK).body(user.writeValueAsString());
	}

}
