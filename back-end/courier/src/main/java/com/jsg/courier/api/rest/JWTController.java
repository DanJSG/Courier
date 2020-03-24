package com.jsg.courier.api.rest;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.courier.utilities.JWTHandler;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/jwt")
public final class JWTController {

	@CrossOrigin(origins = "http://localhost:3000/*")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> createJwt(@RequestBody Map<String, Long> userId) {
		Long id = userId.get("id");
		if(id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(JWTHandler.createToken(id));	
	}
	
	@CrossOrigin(origins = "http://localhost:3000/*")
	@PostMapping(value = "/verify")
	public @ResponseBody ResponseEntity<String> verifyJwt(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
		System.out.println(auth);
		Boolean validity = JWTHandler.verifyToken(auth);
		System.out.println(validity);
		if(!validity) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify JWT.");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Verified JWT.");	
	}
	
}
