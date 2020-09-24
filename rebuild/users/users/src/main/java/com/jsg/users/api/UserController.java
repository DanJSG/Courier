package com.jsg.users.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.users.libs.sql.MySQLRepository;
import com.jsg.users.libs.sql.SQLColumn;
import com.jsg.users.libs.sql.SQLRepository;
import com.jsg.users.libs.sql.SQLTable;
import com.jsg.users.types.User;
import com.jsg.users.types.UserBuilder;

@RestController
public class UserController extends Version1Controller implements RestApi<User, Long> {
	
	@Override
	@GetMapping("/user/get/{id}")
	public ResponseEntity<String> get(@PathVariable("id") Long id) {
		if(id == null || id < 0)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<User> repo = new MySQLRepository<>(SQLTable.USERS);
		List<User> foundUsers = repo.findWhereEqual(SQLColumn.ID, id, 0, new UserBuilder());
		if(foundUsers == null)
			return NOT_FOUND_HTTP_RESPONSE;
		String userResponse = foundUsers.get(0).writeValueAsString();
		if(userResponse == null)
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return ResponseEntity.status(HttpStatus.OK).body(userResponse);
	}
	
	@Override
	@PostMapping("/user/create")
	public ResponseEntity<String> post(@RequestBody User user) {
		if(user == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<User> repo = new MySQLRepository<User>(SQLTable.USERS);
		if(!repo.save(user))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}
	
	@Override
	@PutMapping("/user/update")
	public ResponseEntity<String> put(@RequestBody User user) {
		if(user == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<User> repo = new MySQLRepository<User>(SQLTable.USERS);
		Map<SQLColumn, Object> updateMap = new HashMap<>();
		updateMap.put(SQLColumn.ID, user.getId());
		updateMap.put(SQLColumn.USERNAME, user.getUsername());
		if(!repo.updateWhereEquals(SQLColumn.ID, user.getId(), updateMap))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}
	
	@Override
	@DeleteMapping("/user/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) {
		if(id == null)
			return BAD_REQUEST_HTTP_RESPONSE;
		SQLRepository<User> repo = new MySQLRepository<User>(SQLTable.USERS);
		if(!repo.deleteWhereEquals(SQLColumn.ID, id))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return EMPTY_OK_HTTP_RESPONSE;
	}


}
