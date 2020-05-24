package com.jsg.courier.repositories;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.jsg.courier.httprequests.HttpRequestBuilder;
import com.jsg.courier.httprequests.HttpResponse;

public class UserInfoAPIRepository {
	
	private static final String CLIENT_ID = "ThpDT2t2EDlO";
	private static final String CLIENT_SECRET = "aU3LCC1vzagDBDqEX7O729rJpgStVkH9";
	
	public static HttpResponse getUserInfo(String accessTokenName, String cookieToken, String authHeader,
			long id, String client_id) throws Exception {
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder("http://local.courier.net:8090/api/v1/userInfo");
		requestBuilder.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
		requestBuilder.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
		requestBuilder.addCookie(accessTokenName, cookieToken);
		requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
		requestBuilder.addParameter("id", id);
		requestBuilder.addParameter("client_id", client_id);
		requestBuilder.setRequestMethod(HttpMethod.GET);
		return new HttpResponse(requestBuilder.toHttpURLConnection());
	}
	
}
