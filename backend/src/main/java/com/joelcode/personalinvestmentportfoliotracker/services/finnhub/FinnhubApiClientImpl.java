package com.joelcode.personalinvestmentportfoliotracker.services.finnhub;

import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubCompanyProfileDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.finnhub.FinnhubQuoteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class FinnhubApiClientImpl implements FinnhubApiClient {

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public FinnhubApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public FinnhubQuoteDTO getQuote(String symbol) {
        try {
            String url = String.format("%s/quote?symbol=%s&token=%s", baseUrl, symbol.toUpperCase(), apiKey);
            return restTemplate.getForObject(url, FinnhubQuoteDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch quote for symbol: " + symbol, e);
        }
    }

    @Override
    public FinnhubCompanyProfileDTO getCompanyProfile(String symbol) {
        try {
            String url = String.format("%s/stock/profile2?symbol=%s&token=%s", baseUrl, symbol.toUpperCase(), apiKey);
            return restTemplate.getForObject(url, FinnhubCompanyProfileDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch company profile for symbol: " + symbol, e);
        }
    }

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        FinnhubQuoteDTO quote = getQuote(symbol);
        return quote != null ? quote.getCurrentPrice() : null;
    }
}
