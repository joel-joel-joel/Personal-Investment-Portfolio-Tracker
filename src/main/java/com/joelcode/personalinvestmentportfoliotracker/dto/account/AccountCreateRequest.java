package com.joelcode.personalinvestmentportfoliotracker.dto.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AccountCreateRequest {

    // Account creation request DTO (input)
    @NotBlank(message = "Account name is required")
    @Size(min = 3, max = 50, message = "Account name must be between 3 and 50 characters")
    private final String accountName;

    @NotNull(message = "User id is required")
    private final UUID accountId;

    // Jackson-compatible constructor
    @JsonCreator
    public AccountCreateRequest(
            @JsonProperty("accountName") String accountName,
            @JsonProperty("accountId") UUID accountId) {
        this.accountName = accountName;
        this.accountId = accountId;
    }

    // Getters
    public String getAccountName() {
        return accountName;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
