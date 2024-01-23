package com.example.project3.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
		
	@Override 
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("https://footballmoafront.azurewebsites.net")
			.allowedMethods("GET", "DELETE", "PUT", "POST", "OPTIONS")
			.allowCredentials(true);
	}
	
}
