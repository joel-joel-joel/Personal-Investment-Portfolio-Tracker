package com.joelcode.personalinvestmentportfoliotracker.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This class handles unauthorized access attempts by sending a 401 Unauthorized response
// when a request with an invalid or missing JWT is received.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Handles http requests with unauthorized access
    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized: Authentication token was invalid");
    }
}