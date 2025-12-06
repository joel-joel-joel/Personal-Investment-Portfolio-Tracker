package com.joelcode.personalinvestmentportfoliotracker.services.finnhub;

import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubCompanyProfileDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubQuoteDTO;

import java.math.BigDecimal;

public interface FinnhubApiClient {

    FinnhubQuoteDTO getQuote(String symbol);

    FinnhubCompanyProfileDTO getCompanyProfile(String symbol);

    BigDecimal getCurrentPrice(String symbol);
}
