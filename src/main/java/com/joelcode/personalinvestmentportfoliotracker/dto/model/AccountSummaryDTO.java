package com.joelcode.personalinvestmentportfoliotracker.dto.model;


import com.joelcode.personalinvestmentportfoliotracker.dto.account.HoldingSummaryDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class AccountSummaryDTO {

    // Getters and setters
    // Define key fields
    private UUID accountId;
    private BigDecimal totalInvestedValue;
    private BigDecimal totalMarketValue;
    private BigDecimal totalUnrealizedGain;
    private BigDecimal totalDividends;
    private BigDecimal totalCashBalance;
    private List<HoldingSummaryDTO> holdings;

    // Constructor
    public AccountSummaryDTO(UUID accountId, BigDecimal totalInvestedValue, BigDecimal totalMarketValue,
                             BigDecimal totalUnrealizedGain, BigDecimal totalDividends, BigDecimal totalCashBalance,
                             List<HoldingSummaryDTO> holdings) {
        this.accountId = accountId;
        this.totalInvestedValue = totalInvestedValue;
        this.totalMarketValue = totalMarketValue;
        this.totalUnrealizedGain = totalUnrealizedGain;
        this.totalDividends = totalDividends;
        this.totalCashBalance = totalCashBalance;
    }

    public AccountSummaryDTO() {}
}
