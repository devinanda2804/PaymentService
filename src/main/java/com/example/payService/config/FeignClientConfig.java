package com.example.payService.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Get authentication from the SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if the authentication is available and contains the JWT token in the credentials
            if (authentication != null && authentication.getCredentials() instanceof String) {
                String jwtToken = authentication.getCredentials().toString();
                // Add Authorization header with Bearer token
                requestTemplate.header("Authorization", "Bearer " + jwtToken);
            }
        };
    }
}