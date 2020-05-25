package com.jsg.courier.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.constants.OAuth2;
import com.jsg.courier.httprequests.HttpRequestBuilder;
import com.jsg.courier.httprequests.HttpResponse;

public class UserInfoAPIRepository {
	
	public static HttpResponse getUserInfo(long id, String client_id, String client_secret) throws Exception {
		HttpResponse authResponse = sendTokenRequest(client_id, client_secret, OAuth2.CLIENT_CREDENTIALS_GRANT_TYPE);
		String cookieToken = getCookieToken(authResponse);
		String headerToken = getHeaderToken(authResponse);
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder("http://local.courier.net:8090/api/v1/userInfo");
		requestBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
		requestBuilder.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
		requestBuilder.addCookie(OAuth2.ACCESS_TOKEN_NAME, cookieToken);
		requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + headerToken);
		requestBuilder.addParameter("id", id);
		requestBuilder.addParameter("client_id", client_id);
		requestBuilder.setRequestMethod(HttpMethod.GET);
		return new HttpResponse(requestBuilder.toHttpURLConnection());
	}
	
	private static String getHeaderToken(HttpResponse authResponse) throws JsonMappingException, JsonProcessingException {
		if(authResponse == null || authResponse.getStatus() > 299 || authResponse.getBody() == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, String> tokenMap = new ObjectMapper().readValue(authResponse.getBody(), Map.class);
		return tokenMap.get("token");
	}
	
	private static String getCookieToken(HttpResponse authResponse) {
		if(authResponse == null || authResponse.getStatus() > 299 || authResponse.getBody() == null) {
			return null;
		}
		List<String> cookieHeaders = authResponse.getHeader(HttpHeaders.SET_COOKIE);
		if(cookieHeaders == null || cookieHeaders.size() < 1) {
			return null;
		}
		String cookieHeader = cookieHeaders.get(0);
		if(!cookieHeader.contains(OAuth2.ACCESS_TOKEN_NAME) || !cookieHeader.contains("=") || !cookieHeader.contains(";")) {
			return null;
		}
		return cookieHeader.split("=")[1].split(";")[0];
	}
	
	private static HttpResponse sendTokenRequest(String clientId, String clientSecret, String grantType) throws Exception {
		HttpRequestBuilder authRequestBuilder = new HttpRequestBuilder("http://local.courier.net:8090/api/v1/token");
		authRequestBuilder.addParameter("client_id", clientId);
		authRequestBuilder.addParameter("client_secret", clientSecret);
		authRequestBuilder.addParameter("grant_type", grantType);
		authRequestBuilder.setRequestMethod(HttpMethod.POST);
		return new HttpResponse(authRequestBuilder.toHttpURLConnection());
	}
	
}
