package com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DividendPaymentService {

    DividendPaymentDTO createPayment(DividendPaymentCreateRequest request);

    DividendPaymentDTO getPaymentById(UUID paymentId);

    List<DividendPaymentDTO> getPaymentsByAccount(UUID accountId);

    List<DividendPaymentDTO> getPaymentsByAccountAndStock(UUID accountId, UUID stockId);

    List<DividendPaymentDTO> getPaymentsByAccountInDateRange(UUID accountId,
                                                             LocalDateTime start,
                                                             LocalDateTime end);

    BigDecimal calculateTotalDividendsByAccount(UUID accountId);

    BigDecimal calculateTotalDividendsByAccountAndStock(UUID accountId, UUID stockId);

    List<DividendPaymentDTO> getPendingPayments(UUID accountId);

    void processPaymentsForDividend(UUID dividendId);

    void deletePayment(UUID paymentId);
}