package com.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class CorsConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer{

	@Value("${cors.allowed-origins}")
    private String allowedOrigins;
    
	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/**")
	       
	            .allowedOrigins(allowedOrigins) 
	            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
	            .allowedHeaders("*")
	            .allowCredentials(true)
	            .maxAge(3600);
	}
}