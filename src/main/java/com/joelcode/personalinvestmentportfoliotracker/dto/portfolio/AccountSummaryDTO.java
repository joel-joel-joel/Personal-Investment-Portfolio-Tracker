package com.joelcode.personalinvestmentportfoliotracker.dto.portfolio;


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
        this.holdings = holdings;
    }

    public AccountSummaryDTO() {}

    // Getters and setters
    public UUID getAccountId() {return accountId;}

    public void setAccountId(UUID accountId) {this.accountId = accountId;}

    public BigDecimal getTotalInvestedValue() {return totalInvestedValue;}

    public void setTotalInvestedValue(BigDecimal totalInvestedValue) {this.totalInvestedValue = totalInvestedValue;}

    public BigDecimal getTotalMarketValue() {return totalMarketValue;}

    public void setTotalMarketValue(BigDecimal totalMarketValue) {this.totalMarketValue = totalMarketValue;}

    public BigDecimal getTotalUnrealizedGain() {return totalUnrealizedGain;}

    public void setTotalUnrealizedGain(BigDecimal totalUnrealizedGain) {this.totalUnrealizedGain = totalUnrealizedGain;}

    public BigDecimal getTotalDividends() {return totalDividends;}

    public void setTotalDividends(BigDecimal totalDividends) {this.totalDividends = totalDividends;}

    public BigDecimal getTotalCashBalance() {return totalCashBalance;}

    public void setTotalCashBalance(BigDecimal totalCashBalance) {this.totalCashBalance = totalCashBalance;}

    public List<HoldingSummaryDTO> getHoldings() {return holdings;}

    public void setHoldings(List<HoldingSummaryDTO> holdings) {this.holdings = holdings;}
}
