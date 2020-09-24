package com.jsg.campus.api.config;

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
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/user/get/*").allowedMethods(HttpMethod.GET.toString()).allowedOrigins(origins);
		registry.addMapping("/user/create").allowedMethods(HttpMethod.POST.toString()).allowedOrigins(origins);
		registry.addMapping("/user/update").allowedMethods(HttpMethod.PUT.toString()).allowedOrigins(origins);
		registry.addMapping("/user/delete").allowedMethods(HttpMethod.DELETE.toString()).allowedOrigins(origins);
	}
	
}
