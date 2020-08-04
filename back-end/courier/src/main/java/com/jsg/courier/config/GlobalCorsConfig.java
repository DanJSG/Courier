package com.jsg.courier.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer { 
	
	private static String[] origins;
	
	@Autowired
	public GlobalCorsConfig(@Value("${cors.origins}") String[] origins) {
		GlobalCorsConfig.origins = origins;
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/v1/authorize*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/create*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/getAll*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/getMembers*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/message/getAll*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/search/searchUsers*").allowCredentials(true).allowedOrigins(origins);
	}
	
}
