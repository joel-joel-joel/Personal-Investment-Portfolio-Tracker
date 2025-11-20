package com.joelcode.personalinvestmentportfoliotracker.dto.portfoliosnapshot;

import com.joelcode.personalinvestmentportfoliotracker.entities.PortfolioSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PortfolioSnapshotDTO {

    // Portfolio snapshot response DTO (output)
    private UUID snapshotId;
    private UUID accountId;
    private LocalDate snapshotDate;
    private BigDecimal totalValue;
    private BigDecimal cashBalance;
    private BigDecimal totalInvested;
    private BigDecimal totalGain;
    private BigDecimal totalGainPercent;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercent;
    private BigDecimal marketValue;

    // Constructor
    public PortfolioSnapshotDTO(UUID snapshotId, UUID accountId, LocalDate snapshotDate, BigDecimal totalValue,
                                BigDecimal cashBalance, BigDecimal totalInvested, BigDecimal totalGain,
                                BigDecimal totalGainPercent, BigDecimal dayChange, BigDecimal dayChangePercent,
                                BigDecimal marketValue) {
        this.snapshotId = snapshotId;
        this.accountId = accountId;
        this.snapshotDate = snapshotDate;
        this.totalValue = totalValue;
        this.cashBalance = cashBalance;
        this.totalInvested = totalInvested;
        this.totalGain = totalGain;
        this.totalGainPercent = totalGainPercent;
        this.dayChange = dayChange;
        this.dayChangePercent = dayChangePercent;
        this.marketValue = marketValue;
    }

    public PortfolioSnapshotDTO(PortfolioSnapshot snapshot) {
        this.snapshotId = snapshot.getSnapshotId();
        this.accountId = snapshot.getAccount().getAccountId();
        this.snapshotDate = snapshot.getSnapshotDate();
        this.totalValue = snapshot.getTotalValue();
        this.cashBalance = snapshot.getCashBalance();
        this.totalInvested = snapshot.getTotalInvested();
        this.totalGain = snapshot.getTotalGain();
        this.totalGainPercent = snapshot.getTotalGainPercent();
        this.dayChange = snapshot.getDayChange();
        this.dayChangePercent = snapshot.getDayChangePercent();
        this.marketValue = snapshot.getMarketValue();
    }

    public PortfolioSnapshotDTO() {}

    // Getters and setters
    public UUID getSnapshotId() {return snapshotId;}

    public UUID getAccountId() {return accountId;}

    public LocalDate getSnapshotDate() {return snapshotDate;}

    public BigDecimal getTotalValue() {return totalValue;}

    public BigDecimal getCashBalance() {return cashBalance;}

    public BigDecimal getTotalInvested() {return totalInvested;}

    public BigDecimal getTotalGain() {return totalGain;}

    public BigDecimal getTotalGainPercent() {return totalGainPercent;}

    public BigDecimal getDayChange() {return dayChange;}

    public BigDecimal getDayChangePercent() {return dayChangePercent;}

    public BigDecimal getMarketValue() {return marketValue;}

    public void setSnapshotId(UUID snapshotId) {this.snapshotId = snapshotId;}

    public void setAccountId(UUID accountId) {this.accountId = accountId;}

    public void setSnapshotDate(LocalDate snapshotDate) {this.snapshotDate = snapshotDate;}

    public void setTotalValue(BigDecimal totalValue) {this.totalValue = totalValue;}

    public void setCashBalance(BigDecimal cashBalance) {this.cashBalance = cashBalance;}

    public void setTotalInvested(BigDecimal totalInvested) {this.totalInvested = totalInvested;}

    public void setTotalGain(BigDecimal totalGain) {this.totalGain = totalGain;}

    public void setTotalGainPercent(BigDecimal totalGainPercent) {this.totalGainPercent = totalGainPercent;}

    public void setDayChangePercent(BigDecimal dayChangePercent) {this.dayChangePercent = dayChangePercent;}

    public void setDayChange(BigDecimal dayChange) {this.dayChange = dayChange;}

    public void setMarketValue(BigDecimal marketValue) {this.marketValue = marketValue;}
}