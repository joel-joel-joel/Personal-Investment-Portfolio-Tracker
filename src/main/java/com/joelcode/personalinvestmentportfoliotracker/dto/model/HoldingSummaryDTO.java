package com.joelcode.personalinvestmentportfoliotracker.dto.account;

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
}
