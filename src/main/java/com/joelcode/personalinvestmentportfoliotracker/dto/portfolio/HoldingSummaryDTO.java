package com.joelcode.personalinvestmentportfoliotracker.dto.portfolio;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class HoldingSummaryDTO {

    // Define key fields
    private UUID stockId;
    private String stockCode;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal marketPrice;
    private BigDecimal marketValue;
    private BigDecimal unrealizedGain;

    // Constructors
    public HoldingSummaryDTO(UUID stockId, String stockCode, BigDecimal quantity, BigDecimal averageCost, BigDecimal marketPrice,
                             BigDecimal marketValue, BigDecimal unrealizedGain) {
        this.stockId = stockId;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.marketPrice = marketPrice;
        this.marketValue = marketValue;
        this.unrealizedGain = unrealizedGain;
    }

    public HoldingSummaryDTO() {}

    // Getters and setters
    public UUID getStockId() {return stockId;}

    public void setStockId(UUID stockId) {this.stockId = stockId;}

    public String getStockCode() {return stockCode;}

    public void setStockCode(String stockCode) {this.stockCode = stockCode;}

    public BigDecimal getQuantity() {return quantity;}

    public void setQuantity(BigDecimal quantity) {this.quantity = quantity;}

    public BigDecimal getAverageCost() {return averageCost;}

    public void setAverageCost(BigDecimal averageCost) {this.averageCost = averageCost;}

    public BigDecimal getMarketValue() {return marketValue;}

    public void setMarketValue(BigDecimal marketValue) {this.marketValue = marketValue;}

    public BigDecimal getMarketPrice() {return marketPrice;}

    public void setMarketPrice(BigDecimal marketPrice) {this.marketPrice = marketPrice;}

    public BigDecimal getUnrealizedGain() {return unrealizedGain;}

    public void setUnrealizedGain(BigDecimal unrealizedGain) {this.unrealizedGain = unrealizedGain;}
}
