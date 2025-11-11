package com.joelcode.personalinvestmentportfoliotracker.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "price_history")
public class PriceHistory {

    // Constructor

    public PriceHistory(LocalDateTime closeDate, BigDecimal closePrice, Stock stock) {
        this.stock = stock;
        this.closeDate = closeDate;
        this.closePrice = closePrice;
    }

    public PriceHistory() {}

    // Key fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceHistoryId;

    @Column (nullable = false)
    private LocalDateTime closeDate;

    @Column (nullable = false)
    private BigDecimal closePrice;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "stockId", nullable = false)
    private Stock stock;



    // Getters and setters

    public Long getPriceHistoryId() {
        return priceHistoryId;
    }

    public void setPriceHistoryId(Long priceHistoryId) {
        this.priceHistoryId = priceHistoryId;
    }

    public LocalDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDateTime closeDate) {
        this.closeDate = closeDate;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    // Helper functions (For filtering)

    public boolean isAbove(BigDecimal threshold) {
        return closePrice.compareTo(threshold) > 0;
    }

    public boolean isBelow(BigDecimal threshold) {
        return closePrice.compareTo(threshold) < 0;
    }

}
