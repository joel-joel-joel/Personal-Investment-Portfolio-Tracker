package com.joelcode.personalinvestmentportfoliotracker.dto.model;

import java.math.BigDecimal;

public class AllocationBreakdownDTO {

    // Define key fields
    private String stockCode;
    private BigDecimal percentage;
    private BigDecimal currentValue;

    // Constructors
    public AllocationBreakdownDTO(String stockCode, BigDecimal percentage, BigDecimal currentValue) {
        this.stockCode = stockCode;
        this.percentage = percentage;
        this.currentValue = currentValue;
    }

    public AllocationBreakdownDTO() {}

    // Getters and setters
    public String getStockCode() {return stockCode;}
    public void setStockCode(String stockCode) {this.stockCode = stockCode;}
    public BigDecimal getPercentage() {return percentage;}
    public void setPercentage(BigDecimal percentage) {this.percentage = percentage;}
    public BigDecimal getCurrentValue() {return currentValue;}
    public void setCurrentValue(BigDecimal currentValue) {this.currentValue = currentValue;}
}
