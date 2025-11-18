package com.joelcode.personalinvestmentportfoliotracker.exceptions;

public class CustomAuthenticationException extends RuntimeException {

    // Runtime exception for custom throw and message
    public CustomAuthenticationException(String message) {
        super(message);
    }
}
