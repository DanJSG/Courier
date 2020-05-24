package com.jsg.courier.repositories;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.httprequests.HttpRequestBuilder;
import com.jsg.courier.httprequests.HttpResponse;

public class UserInfoAPIRepository {
	
	private static final String CLIENT_ID = "ThpDT2t2EDlO";
	private static final String CLIENT_SECRET = "aU3LCC1vzagDBDqEX7O729rJpgStVkH9";
	private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
	protected static final String ACCESS_TOKEN_NAME = "acc.tok";
	
	public static HttpResponse getUserInfo(long id) throws Exception {
		
		HttpRequestBuilder authRequestBuilder = new HttpRequestBuilder("http://local.courier.net:8090/api/v1/token");
		authRequestBuilder.addParameter("client_id", CLIENT_ID);
		authRequestBuilder.addParameter("client_secret", CLIENT_SECRET);
		authRequestBuilder.addParameter("grant_type", CLIENT_CREDENTIALS_GRANT_TYPE);
		authRequestBuilder.setRequestMethod(HttpMethod.POST);
		HttpResponse authResponse = new HttpResponse(authRequestBuilder.toHttpURLConnection());
		String headerToken = (String) new ObjectMapper().readValue(authResponse.getBody(), Map.class).get("token");
		String setCookieHeader = authResponse.getHeader(HttpHeaders.SET_COOKIE).get(0);
		String cookieToken = setCookieHeader.split("=")[1].split(";")[0];
		System.out.println("Header token is: " + headerToken);
		System.out.println("Cookie token is: " + cookieToken);
		
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder("http://local.courier.net:8090/api/v1/userInfo");
		requestBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
		requestBuilder.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
		requestBuilder.addCookie(ACCESS_TOKEN_NAME, cookieToken);
		requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + headerToken);
		requestBuilder.addParameter("id", id);
		requestBuilder.addParameter("client_id", CLIENT_ID);
		requestBuilder.setRequestMethod(HttpMethod.GET);
		return new HttpResponse(requestBuilder.toHttpURLConnection());
	}
	
}
