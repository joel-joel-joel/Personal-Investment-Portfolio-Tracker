package com.joelcode.personalinvestmentportfoliotracker.exceptions;

// Runtime exception for entity not found
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
