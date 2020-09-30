package com.jsg.postie.api;

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
		// mappings for saving and fetching messages
		registry.addMapping("/api/v1/chat/{chatId:[a-zA-Z0-9-]+}/messages/get*").allowedMethods(HttpMethod.GET.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/{chatId:[a-zA-Z0-9-]+}/messages/save").allowedMethods(HttpMethod.POST.toString()).allowedOrigins(origins);
	}
	
}
