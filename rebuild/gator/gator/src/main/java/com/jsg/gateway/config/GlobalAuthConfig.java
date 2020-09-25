package com.jsg.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jsg.gateway.auth.AuthorizationInterceptor;

@Configuration
public class GlobalAuthConfig implements WebMvcConfigurer {
	
	@Autowired
	AuthorizationInterceptor authInterceptor;
	
	public void addInterceptors(InterceptorRegistry registry) {	
		registry.addInterceptor(authInterceptor).addPathPatterns(
			// paths here
		);
	}

}
