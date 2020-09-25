package com.jsg.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer { 
	
	private static String[] origins;
	
	@Autowired
	public GlobalCorsConfig(@Value("${CORS_ORIGINS}") String[] origins) {
		GlobalCorsConfig.origins = origins;
	}
	
	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/api/v1/app/getAll*").allowCredentials(true).allowedOrigins(origins);
//		registry.addMapping("/api/v1/app/update*").allowCredentials(true).allowedOrigins(origins).allowedMethods(HttpMethod.PUT.toString());
	}
	
}
