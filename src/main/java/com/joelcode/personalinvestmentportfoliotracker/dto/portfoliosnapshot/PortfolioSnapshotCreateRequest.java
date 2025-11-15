package com.joelcode.personalinvestmentportfoliotracker.dto.portfoliosnapshot;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PortfolioSnapshotCreateRequest {

    // Portfolio snapshot creation request DTO (input)
    @NotNull(message = "Account ID is required")
    private final UUID accountId;

    @NotNull(message = "Snapshot date is required")
    private final LocalDate snapshotDate;

    @NotNull(message = "Total value is required")
    @DecimalMin(value = "0.00", message = "Total value cannot be negative")
    private final BigDecimal totalValue;

    @NotNull(message = "Cash balance is required")
    @DecimalMin(value = "0.00", message = "Cash balance cannot be negative")
    private final BigDecimal cashBalance;

    @NotNull(message = "Total invested is required")
    @DecimalMin(value = "0.00", message = "Total invested cannot be negative")
    private final BigDecimal totalInvested;

    private final BigDecimal totalGainLoss;

    private final BigDecimal dayChange;

    private final BigDecimal dayChangePercent;

    // Constructor
    public PortfolioSnapshotCreateRequest(UUID accountId, LocalDate snapshotDate, BigDecimal totalValue, BigDecimal cashBalance, BigDecimal totalInvested, BigDecimal totalGainLoss, BigDecimal dayChange, BigDecimal dayChangePercent) {
        this.accountId = accountId;
        this.snapshotDate = snapshotDate;
        this.totalValue = totalValue;
        this.cashBalance = cashBalance;
        this.totalInvested = totalInvested;
        this.totalGainLoss = totalGainLoss;
        this.dayChange = dayChange;
        this.dayChangePercent = dayChangePercent;
    }

    // Getters
    public UUID getAccountId() {
        return accountId;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss;
    }

    public BigDecimal getDayChange() {
        return dayChange;
    }

    public BigDecimal getDayChangePercent() {
        return dayChangePercent;
    }
}