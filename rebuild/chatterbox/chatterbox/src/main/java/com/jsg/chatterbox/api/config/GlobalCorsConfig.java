package com.jsg.chatterbox.api.config;

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
		// endpoints for basic chat operations
		registry.addMapping("/api/v1/chat/get/*").allowedMethods(HttpMethod.GET.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/create").allowedMethods(HttpMethod.POST.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/delete/*").allowedMethods(HttpMethod.DELETE.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/update").allowedMethods(HttpMethod.PUT.toString()).allowedOrigins(origins);

		// endpoints for operations relating to specific members of specific chats
		registry.addMapping("/api/v1/chat/{chatId:[a-zA-Z0-9-]+}/member/{userId:[a-zA-Z0-9-]+}/remove").allowedMethods(HttpMethod.DELETE.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/{chatId:[a-zA-Z0-9-]+}/member/add").allowedMethods(HttpMethod.POST.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/chat/{chatId:[a-zA-Z0-9-]+}/members").allowedMethods(HttpMethod.PUT.toString()).allowedOrigins(origins);

		// endpoint for operation involving multiple chats
		registry.addMapping("/api/v1/chats/get/{userId:[a-zA-Z0-9-]+}").allowedMethods(HttpMethod.GET.toString()).allowedOrigins(origins);

		// endpoints for operations relating to members regardless of specific chats
		registry.addMapping("/api/v1/member/get/{userId:[a-zA-Z0-9-]+}").allowedMethods(HttpMethod.GET.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/member/update").allowedMethods(HttpMethod.PUT.toString()).allowedOrigins(origins);
		registry.addMapping("/api/v1/member/delete/{userId:[a-zA-Z0-9-]+}").allowedMethods(HttpMethod.DELETE.toString()).allowedOrigins(origins);

	}
	
}
