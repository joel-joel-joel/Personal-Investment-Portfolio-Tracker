package com.joelcode.personalinvestmentportfoliotracker.dto.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AccountUpdateRequest {

    // Account update request DTO (input)
    // Non-mandatory fields for single variable updates,
    @Size(min = 3, max = 50, message = "Account name must be between 3 and 50 characters")
    private String accountName;

    // Jackson-compatible constructor
    @JsonCreator
    public AccountUpdateRequest(
            @JsonProperty("accountName") String accountName) {
        this.accountName = accountName;
    }

    public AccountUpdateRequest() {}

    // Getters
    public String getAccountName() {return accountName;}

    public void setAccountName(String accountName) {this.accountName = accountName;}
}
