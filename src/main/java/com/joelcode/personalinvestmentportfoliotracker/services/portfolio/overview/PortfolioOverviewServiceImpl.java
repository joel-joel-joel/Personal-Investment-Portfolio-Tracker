package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.overview;

import com.joelcode.personalinvestmentportfoliotracker.dto.holding.HoldingDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.PortfolioOverviewDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.services.portfolio.allocation.AllocationBreakdownService;
import com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment.DividendPaymentService;
import com.joelcode.personalinvestmentportfoliotracker.services.holding.HoldingService;
import com.joelcode.personalinvestmentportfoliotracker.services.account.AccountValidationService;
import com.joelcode.personalinvestmentportfoliotracker.services.portfolio.overview.PortfolioOverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PortfolioOverviewServiceImpl implements PortfolioOverviewService {

    @Autowired
    private AccountValidationService accountValidationService;

    @Autowired
    private HoldingService holdingService;

    @Autowired
    private DividendPaymentService dividendPaymentService;

    @Autowired
    private AllocationBreakdownService allocationService; // optional if needed for breakdown

    @Override
    public PortfolioOverviewDTO getPortfolioOverviewForAccount(java.util.UUID accountId) {
        // Validate account exists
        Account account = accountValidationService.validateAccountExists(accountId);

        // Get holdings as DTOs
        List<HoldingDTO> holdings = holdingService.getHoldingsForAccount(accountId);

        // Calculate total portfolio value
        BigDecimal totalPortfolioValue = holdings.stream()
                .map(h -> h.getCurrentPrice().multiply(h.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total invested
        BigDecimal totalInvested = holdings.stream()
                .map(h -> h.getAverageCostBasis().multiply(h.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate unrealized gain
        BigDecimal totalUnrealizedGain = totalPortfolioValue.subtract(totalInvested);

        // Calculate realized gain (you may need a transaction service for this)
        BigDecimal totalRealizedGain = BigDecimal.ZERO; // placeholder, implement logic if you have realized transactions

        // Calculate total dividends
        BigDecimal totalDividends = dividendPaymentService.getDividendPaymentsForAccount(accountId).stream()
                .map(dto -> dto.getAmountPerShare())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cash balance (assume account has a cashBalance field)
        BigDecimal cashBalance = account.getAccountBalance() != null ? account.getAccountBalance() : BigDecimal.ZERO;

        return new PortfolioOverviewDTO(
                account.getAccountId(),
                totalPortfolioValue,
                totalInvested,
                totalUnrealizedGain,
                totalRealizedGain,
                totalDividends,
                cashBalance,
                holdings
        );
    }
}
