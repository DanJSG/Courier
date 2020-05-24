package com.jsg.courier.httprequests;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class HttpRequestBuilder {
	
	private List<String> parameters;
	private Map<String, String> headers;
	private List<String> cookies;
	private String url;
	private String method;
	private Boolean useCache = true;
	private int timeouts = 0;
	private Boolean allowInput = true;
	private Boolean allowOutput = false;
	private Boolean followRedirects = true;
	
	public HttpRequestBuilder() {
		parameters = new ArrayList<String>();
		headers = new HashMap<String, String>();
		cookies = new ArrayList<String>();
	}
	
	public HttpRequestBuilder(String url) {
		this();
		setUrl(url);
	}
	
	public HttpRequestBuilder(URL url) {
		setUrl(url.toString());
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public <V> void addParameter(String name, V value) {
		parameters.add(name + "=" + value);
	}
	
	public <V> void addParameters(Map<String, V> params) {
		params.forEach((key, value) -> {
			addParameter(key, value);
		});
	}
	
	public void addHeader(String header, String value) {
		headers.put(header, value);
	}
	
	public void addHeaders(Map<String, String> headers) {
		headers.forEach(this.headers::putIfAbsent);
	}
	
	public void addCookie(String cookie, String value) {
		cookies.add(cookie + "=" + value + ";");
	}
	
	public void addCookies(Map<String, String> cookies) {
		cookies.forEach((key, value) -> {
			addCookie(key, value);
		});
	}
	
	public void setRequestMethod(HttpMethod method) {
		this.method = method.toString();
	}
	
	public void shouldUseCache(Boolean bool) {
		this.useCache = bool;
	}
	
	public void setTimeouts(int msTimeout) {
		this.timeouts = msTimeout;
	}
	
	public void allowInput(Boolean bool) {
		this.allowInput = bool;
	}
	
	public void allowOutput(Boolean bool) {
		this.allowOutput = bool;
	}
	
	public void shouldFollowRedirects(Boolean bool) {
		this.followRedirects = bool;
	}
	
	public HttpURLConnection toHttpURLConnection() throws Exception {
		if(this.url == null || this.method == null) {
			throw new Exception();
		}
		String urlWithParamsString = url;
		Boolean firstParamAdded = false;
		if(parameters != null && parameters.size() > 0) {
			for(String param : parameters) {
				if(!firstParamAdded) {
					urlWithParamsString += "?" + param;
					firstParamAdded = true;
				} else {
					urlWithParamsString += "&" + param;
				}
			}
		}
		URL urlWithParams = new URL(urlWithParamsString);
		HttpURLConnection conn = (HttpURLConnection)urlWithParams.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(timeouts);
		conn.setReadTimeout(timeouts);
		conn.setDoInput(allowInput);
		conn.setDoOutput(allowOutput);
		conn.setUseCaches(useCache);
		conn.setInstanceFollowRedirects(followRedirects);
		if(headers != null && headers.size() > 0) {
			headers.forEach((key, value) -> {
				conn.setRequestProperty(key, value);
			});
		}
		if(cookies != null && cookies.size() > 0) {
			String cookiesString = "";
			for(String cookie : cookies) {
				cookiesString += cookie;
			}
			conn.setRequestProperty(HttpHeaders.COOKIE, cookiesString);
		}
		return conn;
	}
	
}
