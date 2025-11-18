package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.summary;

import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.HoldingSummaryDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.AccountSummaryDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.Holding;
import com.joelcode.personalinvestmentportfoliotracker.repositories.AccountRepository;
import com.joelcode.personalinvestmentportfoliotracker.repositories.HoldingRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.dividend.DividendCalculationService;
import com.joelcode.personalinvestmentportfoliotracker.services.stock.StockService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountSummaryServiceImpl implements AccountSummaryService{

    // Define key fields
    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final StockService stockService;
    private final DividendCalculationService dividendCalculationService;

    // Constructor
    public AccountSummaryServiceImpl (AccountRepository accountRepository, HoldingRepository holdingRepository, StockService stockService, DividendCalculationService dividendCalculationService) {
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.stockService = stockService;
        this.dividendCalculationService = dividendCalculationService;
    }

    // Interface functions

    public AccountSummaryDTO getAccountSummary(UUID accountId) {
        Account account = accountRepository.findByAccountId(accountId);

        List<Holding> holdings = holdingRepository.getHoldingsEntitiesByAccount(accountId);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalMarketValue = BigDecimal.ZERO;

        List<HoldingSummaryDTO> holdingSummaries = new ArrayList<>();

        for (Holding h : holdings) {

            BigDecimal currentPrice = stockService.getCurrentPrice(h.getStock().getStockId());
            BigDecimal marketValue = currentPrice.multiply(h.getQuantity());

            BigDecimal invested = h.getAverageCostBasis().multiply(h.getQuantity());
            BigDecimal gain = marketValue.subtract(invested);

            totalInvested = totalInvested.add(invested);
            totalMarketValue = totalMarketValue.add(marketValue);

            HoldingSummaryDTO dto = new HoldingSummaryDTO();
            dto.setStockId(h.getStock().getStockId());
            dto.setStockCode(h.getStock().getStockCode());
            dto.setQuantity(h.getQuantity());
            dto.setAverageCost(h.getAverageCostBasis());
            dto.setMarketPrice(currentPrice);
            dto.setMarketValue(marketValue);
            dto.setUnrealizedGain(gain);

            holdingSummaries.add(dto);

        }

        // calculate dividend
        BigDecimal totalDividends = dividendCalculationService.calculateTotalDividends(accountId);

        // Calculate cash
        BigDecimal cash = account.getAccountBalance() != null ? account.getAccountBalance() : BigDecimal.ZERO;

        // Build summary
        AccountSummaryDTO summary = new AccountSummaryDTO();
        summary.setAccountId(accountId);
        summary.setTotalInvestedValue(totalInvested);
        summary.setTotalMarketValue(totalMarketValue);
        summary.setTotalUnrealizedGain(totalMarketValue.subtract(totalInvested));
        summary.setTotalDividends(totalDividends);
        summary.setTotalCashBalance(cash);
        summary.setHoldings(holdingSummaries);

        return summary;
    }

}
