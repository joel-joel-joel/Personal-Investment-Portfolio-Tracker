package com.joelcode.personalinvestmentportfoliotracker.exceptions;

// Runtime exception for insuffucient balance
class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
