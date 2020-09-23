package com.jsg.gateway.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.gateway.ApiController;
import com.jsg.gateway.RestApi;
import com.jsg.gateway.auth.AuthToken;

@RestController
public class UserController extends ApiController implements RestApi<User, String> {

	@Autowired
	protected UserController(@Value("${ACCESS_TOKEN_SECRET}") String accessTokenSecret) {
		super(accessTokenSecret);
	}

	@Override
	public ResponseEntity<String> get(AuthToken token, String param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> post(AuthToken token, User body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> put(AuthToken token, User body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> delete(AuthToken token, User body) {
		// TODO Auto-generated method stub
		return null;
	}

}
