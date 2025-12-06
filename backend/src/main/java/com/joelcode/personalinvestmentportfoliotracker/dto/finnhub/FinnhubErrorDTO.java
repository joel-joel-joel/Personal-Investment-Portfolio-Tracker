package com.joelcode.personalinvestmentportfoliotracker.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinnhubErrorDTO {

    @JsonProperty("error")
    private String error;

    public FinnhubErrorDTO() {}

    public FinnhubErrorDTO(String error) {
        this.error = error;
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
