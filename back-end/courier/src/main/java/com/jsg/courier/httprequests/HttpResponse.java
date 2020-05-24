package com.jsg.courier.httprequests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpResponse {
	
	private int status;
	private String body;
	private String message;
	private String method;
	private Map<String, List<String>> headers;
	private String url;
	private String queryString;
	
	public HttpResponse(HttpURLConnection connection) throws Exception {
		status = connection.getResponseCode();
		message = connection.getResponseMessage();
		headers = connection.getHeaderFields();
		method = connection.getRequestMethod();
		URL requestUrl = connection.getURL();
		url = requestUrl.toString();
		queryString = requestUrl.getQuery();
		readResponseBody(connection);
	}
	
	private void readResponseBody(HttpURLConnection connection) throws IOException {
		InputStream inputStream;
		if(status > 299) {
			inputStream  = connection.getErrorStream();
		} else {
			inputStream = connection.getInputStream();
		}
		if(inputStream == null) {
			return;
		}
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
		String bodyLine;
		StringBuffer bodyContent = new StringBuffer();
		while ((bodyLine = inputReader.readLine()) != null) {
		    bodyContent.append(bodyLine);
		}
		body = bodyContent.toString();
		inputReader.close();
		connection.disconnect();
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getBody() {
		return body;
	}

	public String getMessage() {
		return message;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public List<String> getHeader(String header) {
		return headers.get(header);
	}
	
	public String getUrl() {
		return url;
	}

	public String getQueryString() {
		return queryString;
	}
	
	
}
