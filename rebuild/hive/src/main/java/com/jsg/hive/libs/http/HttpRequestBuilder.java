package com.jsg.hive.libs.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestBuilder {

    private List<String> parameters;
    private Map<String, String> headers;
    private List<String> cookies;
    private String url;
    private String method;
    private Boolean useCache = true;
    private int timeouts = 0;
    private Boolean allowInput = true;
    private Boolean allowOutput = true;
    private Boolean followRedirects = true;
    private String body;

    public HttpRequestBuilder() {
        parameters = new ArrayList<>();
        headers = new HashMap<>();
        cookies = new ArrayList<>();
    }

    public HttpRequestBuilder(String url) {
        this();
        setUrl(url);
    }

    public HttpRequestBuilder(URL url) {
        setUrl(url.toString());
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public <V> void addParameter(String name, V value) {
        parameters.add(name + "=" + value);
    }

    public <V> void addParameters(Map<String, V> params) {
        params.forEach(this::addParameter);
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
        cookies.forEach(this::addCookie);
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
        if(this.url == null || this.method == null)
            throw new Exception();
        StringBuilder urlBuilder = new StringBuilder(url);
        boolean firstParamAdded = false;
        if(parameters != null && parameters.size() > 0) {
            for(String param : parameters) {
                if(!firstParamAdded) {
                    urlBuilder.append("?").append(param);
                    firstParamAdded = true;
                } else {
                    urlBuilder.append("&").append(param);
                }
            }
        }
        URL urlWithParams = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection)urlWithParams.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(timeouts);
        conn.setReadTimeout(timeouts);
        conn.setDoInput(allowInput);
        conn.setDoOutput(allowOutput);
        conn.setUseCaches(useCache);
        conn.setInstanceFollowRedirects(followRedirects);
        if (body != null) {
            OutputStream out = conn.getOutputStream();
            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
            out.write(bodyBytes, 0, bodyBytes.length);
        }
        if(headers != null && headers.size() > 0) {
            headers.forEach(conn::setRequestProperty);
        }
        StringBuilder cookieStringBuilder = new StringBuilder();
        if(cookies != null && cookies.size() > 0) {
            for(String cookie : cookies) {
                cookieStringBuilder.append(cookie);
            }
            conn.setRequestProperty(HttpHeaders.COOKIE, cookieStringBuilder.toString());
        }
        return conn;
    }

}
