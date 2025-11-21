package com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.entities.DividendPayment;
import com.joelcode.personalinvestmentportfoliotracker.repositories.AccountRepository;
import com.joelcode.personalinvestmentportfoliotracker.repositories.DividendPaymentRepository;
import com.joelcode.personalinvestmentportfoliotracker.repositories.DividendRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Profile("!test")
public class DividendPaymentValidationService {

    private final DividendPaymentRepository paymentRepository;
    private final DividendRepository dividendRepository;
    private final AccountRepository accountRepository;

    public DividendPaymentValidationService(DividendPaymentRepository paymentRepository,
                                            DividendRepository dividendRepository,
                                            AccountRepository accountRepository) {
        this.paymentRepository = paymentRepository;
        this.dividendRepository = dividendRepository;
        this.accountRepository = accountRepository;
    }

    public DividendPayment validatePaymentExists(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Dividend payment not found with ID: " + paymentId));
    }

    public void validateAccountExists(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found with ID: " + accountId);
        }
    }

    public void validateCreateRequest(DividendPaymentCreateRequest request) {
        if (request.getAccountId() == null) {
            throw new RuntimeException("Account ID cannot be null");
        }
        if (request.getDividendId() == null) {
            throw new RuntimeException("Dividend ID cannot be null");
        }
        validateShareQuantity(request.getShareQuantity());
    }

    public void validateShareQuantity(BigDecimal shareQuantity) {
        if (shareQuantity == null || shareQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Share quantity must be greater than zero");
        }
    }

    public void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new RuntimeException("Start and end dates cannot be null");
        }
        if (start.isAfter(end)) {
            throw new RuntimeException("Start date must be before end date");
        }
    }
}