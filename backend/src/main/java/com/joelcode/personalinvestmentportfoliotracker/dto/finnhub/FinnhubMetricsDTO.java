package com.joelcode.personalinvestmentportfoliotracker.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubMetricsDTO {

    @JsonProperty("metric")
    private MetricData metric;

    @JsonProperty("series")
    private Object series; // Not used but part of response

    public FinnhubMetricsDTO() {}

    public MetricData getMetric() { return metric; }
    public void setMetric(MetricData metric) { this.metric = metric; }

    public Object getSeries() { return series; }
    public void setSeries(Object series) { this.series = series; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetricData {

        // Price Performance Metrics
        @JsonProperty("52WeekHigh")
        private BigDecimal fiftyTwoWeekHigh;

        @JsonProperty("52WeekLow")
        private BigDecimal fiftyTwoWeekLow;

        @JsonProperty("52WeekHighDate")
        private String fiftyTwoWeekHighDate;

        @JsonProperty("52WeekLowDate")
        private String fiftyTwoWeekLowDate;

        @JsonProperty("52WeekPriceReturnDaily")
        private BigDecimal fiftyTwoWeekPriceReturn;

        // Fundamental Metrics
        @JsonProperty("peExclExtraTTM")
        private BigDecimal peRatio;

        @JsonProperty("pbQuarterly")
        private BigDecimal priceToBookRatio;

        @JsonProperty("psAnnual")
        private BigDecimal priceToSalesRatio;

        @JsonProperty("epsExclExtraItemsTTM")
        private BigDecimal eps;

        @JsonProperty("dividendYieldIndicatedAnnual")
        private BigDecimal dividendYield;

        @JsonProperty("bookValuePerShareQuarterly")
        private BigDecimal bookValuePerShare;

        @JsonProperty("revenuePerShareTTM")
        private BigDecimal revenuePerShare;

        // Market Metrics
        @JsonProperty("marketCapitalization")
        private BigDecimal marketCap;

        @JsonProperty("beta")
        private BigDecimal beta;

        @JsonProperty("10DayAverageTradingVolume")
        private BigDecimal tenDayAverageVolume;

        @JsonProperty("currentRatioQuarterly")
        private BigDecimal currentRatio;

        // Profitability Metrics
        @JsonProperty("roaeTTM")
        private BigDecimal returnOnAverageEquity;

        @JsonProperty("roiTTM")
        private BigDecimal returnOnInvestment;

        @JsonProperty("grossMarginTTM")
        private BigDecimal grossMargin;

        @JsonProperty("netMarginTTM")
        private BigDecimal netMargin;

        public MetricData() {}

        // Getters and Setters
        public BigDecimal getFiftyTwoWeekHigh() { return fiftyTwoWeekHigh; }
        public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) { this.fiftyTwoWeekHigh = fiftyTwoWeekHigh; }

        public BigDecimal getFiftyTwoWeekLow() { return fiftyTwoWeekLow; }
        public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) { this.fiftyTwoWeekLow = fiftyTwoWeekLow; }

        public String getFiftyTwoWeekHighDate() { return fiftyTwoWeekHighDate; }
        public void setFiftyTwoWeekHighDate(String fiftyTwoWeekHighDate) { this.fiftyTwoWeekHighDate = fiftyTwoWeekHighDate; }

        public String getFiftyTwoWeekLowDate() { return fiftyTwoWeekLowDate; }
        public void setFiftyTwoWeekLowDate(String fiftyTwoWeekLowDate) { this.fiftyTwoWeekLowDate = fiftyTwoWeekLowDate; }

        public BigDecimal getFiftyTwoWeekPriceReturn() { return fiftyTwoWeekPriceReturn; }
        public void setFiftyTwoWeekPriceReturn(BigDecimal fiftyTwoWeekPriceReturn) { this.fiftyTwoWeekPriceReturn = fiftyTwoWeekPriceReturn; }

        public BigDecimal getPeRatio() { return peRatio; }
        public void setPeRatio(BigDecimal peRatio) { this.peRatio = peRatio; }

        public BigDecimal getPriceToBookRatio() { return priceToBookRatio; }
        public void setPriceToBookRatio(BigDecimal priceToBookRatio) { this.priceToBookRatio = priceToBookRatio; }

        public BigDecimal getPriceToSalesRatio() { return priceToSalesRatio; }
        public void setPriceToSalesRatio(BigDecimal priceToSalesRatio) { this.priceToSalesRatio = priceToSalesRatio; }

        public BigDecimal getEps() { return eps; }
        public void setEps(BigDecimal eps) { this.eps = eps; }

        public BigDecimal getDividendYield() { return dividendYield; }
        public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }

        public BigDecimal getBookValuePerShare() { return bookValuePerShare; }
        public void setBookValuePerShare(BigDecimal bookValuePerShare) { this.bookValuePerShare = bookValuePerShare; }

        public BigDecimal getRevenuePerShare() { return revenuePerShare; }
        public void setRevenuePerShare(BigDecimal revenuePerShare) { this.revenuePerShare = revenuePerShare; }

        public BigDecimal getMarketCap() { return marketCap; }
        public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }

        public BigDecimal getBeta() { return beta; }
        public void setBeta(BigDecimal beta) { this.beta = beta; }

        public BigDecimal getTenDayAverageVolume() { return tenDayAverageVolume; }
        public void setTenDayAverageVolume(BigDecimal tenDayAverageVolume) { this.tenDayAverageVolume = tenDayAverageVolume; }

        public BigDecimal getCurrentRatio() { return currentRatio; }
        public void setCurrentRatio(BigDecimal currentRatio) { this.currentRatio = currentRatio; }

        public BigDecimal getReturnOnAverageEquity() { return returnOnAverageEquity; }
        public void setReturnOnAverageEquity(BigDecimal returnOnAverageEquity) { this.returnOnAverageEquity = returnOnAverageEquity; }

        public BigDecimal getReturnOnInvestment() { return returnOnInvestment; }
        public void setReturnOnInvestment(BigDecimal returnOnInvestment) { this.returnOnInvestment = returnOnInvestment; }

        public BigDecimal getGrossMargin() { return grossMargin; }
        public void setGrossMargin(BigDecimal grossMargin) { this.grossMargin = grossMargin; }

        public BigDecimal getNetMargin() { return netMargin; }
        public void setNetMargin(BigDecimal netMargin) { this.netMargin = netMargin; }
    }
}
