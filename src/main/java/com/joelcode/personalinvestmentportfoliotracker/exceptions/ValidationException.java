package com.joelcode.personalinvestmentportfoliotracker.exceptions;

// Runtime exception for validation errors
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}