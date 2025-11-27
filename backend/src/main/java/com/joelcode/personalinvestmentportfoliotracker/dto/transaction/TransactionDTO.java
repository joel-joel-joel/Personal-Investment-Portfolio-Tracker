package com.joelcode.personalinvestmentportfoliotracker.dto.transaction;

import com.joelcode.personalinvestmentportfoliotracker.entities.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionDTO {

    // Transaction response DTO (output)
    private UUID transactionId;
    private UUID stockId;
    private UUID accountId;
    private BigDecimal shareQuantity;
    private BigDecimal pricePerShare;
    private Transaction.TransactionType transactionType;

    // Constructors
    public TransactionDTO(UUID transactionId, UUID stockId, UUID accountId, BigDecimal shareQuantity, BigDecimal pricePerShare, Transaction.TransactionType transactionType) {
        this.transactionId = transactionId;
        this.stockId = stockId;
        this.accountId = accountId;
        this.shareQuantity = shareQuantity;
        this.pricePerShare = pricePerShare;
        this.transactionType = transactionType;
    }

    public TransactionDTO(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.stockId = transaction.getStock().getStockId();
        this.accountId = transaction.getAccount().getAccountId();
        this.shareQuantity = transaction.getShareQuantity();
        this.pricePerShare = transaction.getPricePerShare();
        this.transactionType = transaction.getTransactionType();
    }

    public TransactionDTO() {};


    // Getters and setters
    public UUID getTransactionId() {return transactionId;}

    public UUID getStockCode() {return stockId;}

    public UUID getAccountId() {return accountId;}

    public BigDecimal getShareQuantity() {return shareQuantity;}

    public BigDecimal getPricePerShare() {return pricePerShare;}

    public Transaction.TransactionType getTransactionType() {return transactionType;}

    public void setTransactionId(UUID transactionId) {this.transactionId = transactionId;}

    public void setStockCode(UUID stockCode) {this.stockId = stockCode;}

    public void setAccountId(UUID accountId) {this.accountId = accountId;}

    public void setShareQuantity(BigDecimal shareQuantity) {this.shareQuantity = shareQuantity;}

    public void setPricePerShare(BigDecimal pricePerShare) {this.pricePerShare = pricePerShare;}

    public void setTransactionType(Transaction.TransactionType transactionType) {this.transactionType = transactionType;}
}
