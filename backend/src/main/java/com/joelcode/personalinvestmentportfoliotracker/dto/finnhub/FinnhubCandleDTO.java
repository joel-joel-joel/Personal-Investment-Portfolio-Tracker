package com.joelcode.personalinvestmentportfoliotracker.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubCandleDTO {

    @JsonProperty("c")
    private List<BigDecimal> closePrices;

    @JsonProperty("h")
    private List<BigDecimal> highPrices;

    @JsonProperty("l")
    private List<BigDecimal> lowPrices;

    @JsonProperty("o")
    private List<BigDecimal> openPrices;

    @JsonProperty("t")
    private List<Long> timestamps;

    @JsonProperty("v")
    private List<Long> volumes;

    @JsonProperty("s")
    private String status;

    public FinnhubCandleDTO() {}

    public FinnhubCandleDTO(List<BigDecimal> closePrices, List<BigDecimal> highPrices,
                            List<BigDecimal> lowPrices, List<BigDecimal> openPrices,
                            List<Long> timestamps, List<Long> volumes, String status) {
        this.closePrices = closePrices;
        this.highPrices = highPrices;
        this.lowPrices = lowPrices;
        this.openPrices = openPrices;
        this.timestamps = timestamps;
        this.volumes = volumes;
        this.status = status;
    }

    // Getters and Setters
    public List<BigDecimal> getClosePrices() { return closePrices; }
    public void setClosePrices(List<BigDecimal> closePrices) { this.closePrices = closePrices; }

    public List<BigDecimal> getHighPrices() { return highPrices; }
    public void setHighPrices(List<BigDecimal> highPrices) { this.highPrices = highPrices; }

    public List<BigDecimal> getLowPrices() { return lowPrices; }
    public void setLowPrices(List<BigDecimal> lowPrices) { this.lowPrices = lowPrices; }

    public List<BigDecimal> getOpenPrices() { return openPrices; }
    public void setOpenPrices(List<BigDecimal> openPrices) { this.openPrices = openPrices; }

    public List<Long> getTimestamps() { return timestamps; }
    public void setTimestamps(List<Long> timestamps) { this.timestamps = timestamps; }

    public List<Long> getVolumes() { return volumes; }
    public void setVolumes(List<Long> volumes) { this.volumes = volumes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
