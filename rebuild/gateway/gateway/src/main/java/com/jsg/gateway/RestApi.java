package com.jsg.gateway;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsg.gateway.auth.AuthToken;

public interface RestApi<T, V> {
	
	public ResponseEntity<String> get(@RequestHeader(HttpHeaders.AUTHORIZATION) AuthToken token, @Nullable @RequestParam V param);
	
	public ResponseEntity<String> post(@RequestHeader(HttpHeaders.AUTHORIZATION) AuthToken token, @RequestBody T body);
	
	public ResponseEntity<String> put(@RequestHeader(HttpHeaders.AUTHORIZATION) AuthToken token, @RequestBody T body);
	
	public ResponseEntity<String> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) AuthToken token, @RequestBody T body);
	
}
