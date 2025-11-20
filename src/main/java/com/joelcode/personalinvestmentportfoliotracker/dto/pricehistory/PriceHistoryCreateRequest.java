package com.joelcode.personalinvestmentportfoliotracker.dto.pricehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PriceHistoryCreateRequest {

    // Price history creation request (input)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull (message = "Close date is required")
    private LocalDateTime closeDate;

    @NotNull (message = "Stock ID is required")
    private UUID stockId;

    @NotNull (message = "Close price is required")
    @PositiveOrZero (message = "Close price must be positive")
    private BigDecimal closePrice;

    // Jackson-compatible constructor
    @JsonCreator
    public PriceHistoryCreateRequest(
            @JsonProperty ("closeDate") LocalDateTime closeDate,
            @JsonProperty ("closePrice") UUID stockId,
            @JsonProperty ("closePrice") BigDecimal closePrice) {
        this.closeDate = closeDate;
        this.stockId = stockId;
        this.closePrice = closePrice;
    }

    public PriceHistoryCreateRequest() {}

    // Getters
    public LocalDateTime getCloseDate() {return closeDate;}

    public UUID getStockId() {return stockId;}

    public BigDecimal getClosePrice() {return closePrice;}

    public void setCloseDate(LocalDateTime closeDate) {this.closeDate = closeDate;}

    public void setStockId(UUID stockId) {this.stockId = stockId;}

    public void setClosePrice(BigDecimal closePrice) {this.closePrice = closePrice;}


}
