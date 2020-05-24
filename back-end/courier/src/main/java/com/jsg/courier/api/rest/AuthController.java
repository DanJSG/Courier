package com.jsg.courier.api.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.UserInfo;
import com.jsg.courier.repositories.UserInfoRepository;
import com.jsg.courier.utilities.AuthHeaderHandler;
import com.jsg.courier.utilities.JWTHandler;
import com.mongodb.util.JSON;

@RestController
public class AuthController extends ApiController {
	
	@Autowired
	public AuthController(@Value("${token.secret.access}") String accessTokenSecret,
			@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenSecret, sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/authorize")
	public @ResponseBody ResponseEntity<String> authorize(@CookieValue(name = ACCESS_TOKEN_NAME, required = false) String jwt, 
			@RequestHeader String authorization) throws Exception {
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		System.out.println(headerJwt);
		System.out.println(jwt);
		if(!JWTHandler.tokenIsValid(jwt, ACCESS_TOKEN_SECRET) || 
				!JWTHandler.tokenIsValid(headerJwt, ACCESS_TOKEN_SECRET)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		
		long id2 = JWTHandler.getIdFromToken(headerJwt);
		URL url = new URL("http://local.courier.net:8090/api/v1/userInfo?client_id=ThpDT2t2EDlO&id=" + id2);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE,"application/json");
        connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
		connection.setRequestProperty(HttpHeaders.COOKIE, ACCESS_TOKEN_NAME + "=" + jwt);
		System.out.println(authorization);
		connection.setRequestProperty(HttpHeaders.AUTHORIZATION, authorization);
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(15000);
		int status = connection.getResponseCode();
		System.out.println(status);
		InputStream stream;
		if(status > 299) {
			stream  = connection.getErrorStream();
		} else {
			stream = connection.getInputStream();
		}
		String responseBody = null;
		if(stream != null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(stream));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			responseBody = content.toString();
			in.close();
			System.out.println("Response body is:");
			System.out.println(responseBody);	
		} else {
			System.out.println("No error body");
		}
		connection.disconnect();
		
//		UserInfoRepository repo = new UserInfoRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
//		long id = JWTHandler.getIdFromToken(headerJwt);
//		List<UserInfo> infoList = repo.findWhereEqual("id", id, 1);
//		if(infoList == null || infoList.size() < 1) {
//			return UNAUTHORIZED_HTTP_RESPONSE;
//		}
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}

}
