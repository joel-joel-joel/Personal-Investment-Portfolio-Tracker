package com.joelcode.personalinvestmentportfoliotracker.dto.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.joelcode.personalinvestmentportfoliotracker.entities.Transaction;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionCreateRequest {

    // Transaction request creation DTO (input)
    @NotBlank (message = "Stock code is required")
    private UUID stockId;

    @NotNull(message = "Account id is required")
    private UUID accountId;

    @NotNull(message = "Share quantity is required")
    @Positive (message = "Share quantity must be positive or zero")
    private BigDecimal shareQuantity;

    @NotNull (message = "Price per share is required")
    @Positive(message = "Price per share must be positive or zero")
    private BigDecimal pricePerShare;

    @NotNull (message = "Transaction type is required")
    private Transaction.TransactionType transactionType;



    // Jackson-compatible constructor (and also used by tests)
    @JsonCreator
    public TransactionCreateRequest(
            @JsonProperty ("accountId") UUID accountId,
            @JsonProperty ("stockId") UUID stockId,
            @JsonProperty ("shareQuantity") BigDecimal shareQuantity,
            @JsonProperty ("pricePerShare") BigDecimal pricePerShare,
            @JsonProperty ("transactionType") Transaction.TransactionType transactionType) {
        this.stockId = stockId;
        this.accountId = accountId;
        this.shareQuantity = shareQuantity;
        this.pricePerShare = pricePerShare;
        this.transactionType = transactionType;
    }

    public TransactionCreateRequest() {};

    // Getters and setters
    public UUID getStockId() {return stockId;}

    public UUID getAccountId() {return accountId;}

    public BigDecimal getShareQuantity() {return shareQuantity;}

    public BigDecimal getPricePerShare() {return pricePerShare;}

    public Transaction.TransactionType getTransactionType() {return transactionType;}

    @AssertTrue(message = "Transaction amount must be positive")
    public boolean isValidAmount() {
        return shareQuantity != null &&
                pricePerShare != null &&
                shareQuantity.compareTo(BigDecimal.ZERO) > 0 &&
                pricePerShare.compareTo(BigDecimal.ZERO) > 0;
    }

    public void setStockId(UUID stockId) {this.stockId = stockId;}

    public void setAccountId(UUID accountId) {this.accountId = accountId;}

    public void setShareQuantity(BigDecimal shareQuantity) {this.shareQuantity = shareQuantity;}

    public void setPricePerShare(BigDecimal pricePerShare) {this.pricePerShare = pricePerShare;}

    public void setTransactionType(Transaction.TransactionType transactionType) {this.transactionType = transactionType;}

}
