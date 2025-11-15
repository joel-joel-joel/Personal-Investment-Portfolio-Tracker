package com.joelcode.personalinvestmentportfoliotracker.dto.holding;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class HoldingUpdateRequest {

    // Holding update request DTO (input)
    // Non-mandatory fields for single variable updates
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @DecimalMin(value = "0.01", message = "Average cost basis must be greater than zero")
    private BigDecimal averageCostBasis;

    @DecimalMin(value = "0.01", message = "Total cost basis must be greater than zero")
    private BigDecimal totalCostBasis;

    private BigDecimal realizedGainLoss;

    // Constructor
    public HoldingUpdateRequest(BigDecimal quantity, BigDecimal averageCostBasis, BigDecimal totalCostBasis, BigDecimal realizedGainLoss) {
        this.quantity = quantity;
        this.averageCostBasis = averageCostBasis;
        this.totalCostBasis = totalCostBasis;
        this.realizedGainLoss = realizedGainLoss;
    }

    public HoldingUpdateRequest() {}

    // Getters and setters
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAverageCostBasis() {
        return averageCostBasis;
    }

    public void setAverageCostBasis(BigDecimal averageCostBasis) {
        this.averageCostBasis = averageCostBasis;
    }

    public BigDecimal getTotalCostBasis() {
        return totalCostBasis;
    }

    public void setTotalCostBasis(BigDecimal totalCostBasis) {
        this.totalCostBasis = totalCostBasis;
    }

    public BigDecimal getRealizedGainLoss() {
        return realizedGainLoss;
    }

    public void setRealizedGainLoss(BigDecimal realizedGainLoss) {
        this.realizedGainLoss = realizedGainLoss;
    }
}