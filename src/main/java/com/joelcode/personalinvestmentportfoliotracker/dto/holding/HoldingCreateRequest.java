package com.joelcode.personalinvestmentportfoliotracker.dto.holding;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class HoldingCreateRequest {

    // Holding creation request DTO (input)
    @NotNull(message = "Account ID is required")
    private final UUID accountId;

    @NotNull(message = "Stock ID is required")
    private final UUID stockId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than zero")
    private final BigDecimal quantity;

    @NotNull(message = "Average cost basis is required")
    @DecimalMin(value = "0.01", message = "Average cost basis must be greater than zero")
    private final BigDecimal averageCostBasis;

    @NotNull(message = "Total cost basis is required")
    @DecimalMin(value = "0.01", message = "Total cost basis must be greater than zero")
    private final BigDecimal totalCostBasis;

    // Constructor
    public HoldingCreateRequest(UUID accountId, UUID stockId, BigDecimal quantity, BigDecimal averageCostBasis, BigDecimal totalCostBasis) {
        this.accountId = accountId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.averageCostBasis = averageCostBasis;
        this.totalCostBasis = totalCostBasis;
    }

    // Getters
    public UUID getAccountId() {
        return accountId;
    }

    public UUID getStockId() {
        return stockId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCostBasis() {
        return averageCostBasis;
    }

    public BigDecimal getTotalCostBasis() {
        return totalCostBasis;
    }
}