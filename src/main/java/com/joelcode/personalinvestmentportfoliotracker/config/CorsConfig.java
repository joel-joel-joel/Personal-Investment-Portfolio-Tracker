package com.joelcode.personalinvestmentportfoliotracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// Centralized CORS Configuration. This configuration allows frontend application running on a different
// origin (domain/port) to make requests to this backend API. This is used by both the websocket and REST controllers

@Configuration
public class CorsConfig {

    // Inject allowed origins from application.yml
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,HEAD,PATCH}")
    private String[] allowedMethods;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    // Configure CORS globally for all endpoint. Defines what http or frontend requests are allowed to communicate to
    // backend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins
        // DEVELOPMENT: Allow localhost with common ports
        // PRODUCTION: Replace with actual frontend domain
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",     // React default
                "http://localhost:4200",     // Angular default
                "http://localhost:8081",     // Alternative dev port
                "http://localhost:5173",     // Vite default
                "https://your-frontend-domain.com"  // TODO: Update for production
        ));

        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "HEAD",
                "PATCH"
        ));

        // Allow standard headers plus custom ones
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",        // For JWT tokens
                "Content-Type",         // For JSON payloads
                "Accept",              // For response format
                "Origin",              // CORS origin header
                "X-Requested-With",    // For AJAX requests
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-XSRF-TOKEN"         // For CSRF protection
        ));

        // Headers that the frontend can access in the response
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",       // For pagination
                "X-Page-Number",       // For pagination
                "X-Page-Size"          // For pagination
        ));

        // Allow cookies and authorization headers
        configuration.setAllowCredentials(true);

        // How long (in seconds) the browser can cache preflight response
        configuration.setMaxAge(maxAge);

        // Register all the cors allowances to the controllers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}