package com.joelcode.personalinvestmentportfoliotracker.dto.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class AccountUpdateRequest {

    @Size(min = 3, max = 50, message = "Account name must be between 3 and 50 characters")
    private String accountName;

    private BigDecimal cashBalance;  // ← ADD THIS

    @JsonCreator
    public AccountUpdateRequest(
            @JsonProperty("accountName") String accountName,
            @JsonProperty("cashBalance") BigDecimal cashBalance) {  // ← ADD THIS PARAM
        this.accountName = accountName;
        this.cashBalance = cashBalance;  // ← ADD THIS
    }

    public AccountUpdateRequest() {}

    // Existing getters/setters
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    // ← ADD THESE GETTERS/SETTERS
    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }
}