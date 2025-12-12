package com.joelcode.personalinvestmentportfoliotracker.dto.transaction;

import com.joelcode.personalinvestmentportfoliotracker.entities.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDTO {

    // Transaction response DTO (output)
    @JsonProperty("transactionId")
    private UUID transactionId;

    @JsonProperty("stockId")
    private UUID stockId;

    @JsonProperty("accountId")
    private UUID accountId;

    @JsonProperty("shareQuantity")
    private BigDecimal shareQuantity;

    @JsonProperty("pricePerShare")
    private BigDecimal pricePerShare;

    @JsonProperty("transactionType")
    private Transaction.TransactionType transactionType;

    @JsonProperty("transactionDate")
    private LocalDateTime transactionDate;

    // Constructors
    public TransactionDTO(UUID transactionId, UUID stockId, UUID accountId, BigDecimal shareQuantity, BigDecimal pricePerShare, Transaction.TransactionType transactionType, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.stockId = stockId;
        this.accountId = accountId;
        this.shareQuantity = shareQuantity;
        this.pricePerShare = pricePerShare;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    public TransactionDTO(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.stockId = transaction.getStock().getStockId();
        this.accountId = transaction.getAccount().getAccountId();
        this.shareQuantity = transaction.getShareQuantity();
        this.pricePerShare = transaction.getPricePerShare();
        this.transactionType = transaction.getTransactionType();
        this.transactionDate = transaction.getCreatedAt();
    }

    public TransactionDTO() {};


    // Getters and setters
    public UUID getTransactionId() {return transactionId;}

    public UUID getStockCode() {return stockId;}

    public UUID getAccountId() {return accountId;}

    public BigDecimal getShareQuantity() {return shareQuantity;}

    public BigDecimal getPricePerShare() {return pricePerShare;}

    public Transaction.TransactionType getTransactionType() {return transactionType;}

    public LocalDateTime getTransactionDate() {return transactionDate;}

    public void setTransactionId(UUID transactionId) {this.transactionId = transactionId;}

    public void setStockCode(UUID stockCode) {this.stockId = stockCode;}

    public void setAccountId(UUID accountId) {this.accountId = accountId;}

    public void setShareQuantity(BigDecimal shareQuantity) {this.shareQuantity = shareQuantity;}

    public void setPricePerShare(BigDecimal pricePerShare) {this.pricePerShare = pricePerShare;}

    public void setTransactionType(Transaction.TransactionType transactionType) {this.transactionType = transactionType;}

    public void setTransactionDate(LocalDateTime transactionDate) {this.transactionDate = transactionDate;}
}
