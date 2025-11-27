package com.joelcode.personalinvestmentportfoliotracker.exceptions;

// Runtime exception for duplicate entities
class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}