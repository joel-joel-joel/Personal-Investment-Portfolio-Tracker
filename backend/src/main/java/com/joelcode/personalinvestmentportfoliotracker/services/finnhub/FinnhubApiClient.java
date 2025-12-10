package com.joelcode.personalinvestmentportfoliotracker.services.finnhub;

import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubCandleDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubCompanyProfileDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubMetricsDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubQuoteDTO;

import java.math.BigDecimal;

public interface FinnhubApiClient {

    FinnhubQuoteDTO getQuote(String symbol);

    FinnhubCompanyProfileDTO getCompanyProfile(String symbol);

    FinnhubMetricsDTO getMetrics(String symbol);

    FinnhubCandleDTO getCandles(String symbol, String resolution, long from, long to);

    BigDecimal getCurrentPrice(String symbol);
}
