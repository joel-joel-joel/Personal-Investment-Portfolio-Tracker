package com.joelcode.personalinvestmentportfoliotracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch authentication errors and return clean JSON error messages

    // Custom failed authentication message
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<?> handleCustomAuthException(CustomAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "AUTHENTICATION_FAILED",
                        "message", ex.getMessage()
                )
        );
    }

    // Incorrect username/pw messsage
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "BAD_CREDENTIALS",
                        "message", "Invalid username or password"
                )
        );
    }

    // Failed authentication message
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "AUTH_ERROR",
                        "message", ex.getMessage()
                )
        );
    }
}
