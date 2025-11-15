package com.joelcode.personalinvestmentportfoliotracker.services.dividend;

import com.joelcode.personalinvestmentportfoliotracker.entities.Dividend;
import com.joelcode.personalinvestmentportfoliotracker.repositories.DividendRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DividendValidationService {

    private final DividendRepository dividendRepository;

    public DividendValidationService(DividendRepository dividendRepository) {
        this.dividendRepository = dividendRepository;
    }

    public Dividend validateDividendExists(UUID dividendId) {
        return dividendRepository.findById(dividendId)
                .orElseThrow(() -> new RuntimeException("Dividend not found with ID: " + dividendId));
    }

    public void validateCreateRequest(BigDecimal amount, LocalDateTime paymentDate) {
        validateAmount(amount);
        validatePaymentDate(paymentDate);
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Dividend amount must be greater than zero.");
        }
    }

    public void validatePaymentDate(LocalDateTime paymentDate) {
        if (paymentDate == null) {
            throw new RuntimeException("Payment date cannot be null.");
        }
        if (paymentDate.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Payment date cannot be in the future.");
        }
    }
}
