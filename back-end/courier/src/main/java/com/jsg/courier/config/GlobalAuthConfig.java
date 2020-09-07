package com.jsg.courier.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jsg.courier.auth.AuthorizationInterceptor;

@Configuration
public class GlobalAuthConfig implements WebMvcConfigurer {
	
	@Autowired
	AuthorizationInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor).addPathPatterns(
				"/api/v1/authorize*",
				"/api/v1/chat/create*",
				"/api/v1/chat/getAll",
				"/api/v1/chat/getMembers*",
				"/api/v1/message/getAll*",
				"/api/v1/user/search*"
		);
	}

}
