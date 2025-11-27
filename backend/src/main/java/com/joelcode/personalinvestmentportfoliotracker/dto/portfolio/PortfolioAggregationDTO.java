package com.joelcode.personalinvestmentportfoliotracker.dto.portfolio;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class PortfolioAggregationDTO {

    // Portfolio aggregation response DTO (output)
    private UUID accountId;
    private UUID userId;
    private BigDecimal totalValue;
    private BigDecimal totalDividends;
    private int numberOfHoldings;
    private List<AllocationBreakdownDTO> allocations;

    // Constructors
    public PortfolioAggregationDTO() {}

    public PortfolioAggregationDTO(UUID accountId, UUID userId, BigDecimal totalValue,
                                   BigDecimal totalDividends, int numberOfHoldings,
                                   List<AllocationBreakdownDTO> allocations) {
        this.accountId = accountId;
        this.userId = userId;
        this.totalValue = totalValue;
        this.totalDividends = totalDividends;
        this.numberOfHoldings = numberOfHoldings;
        this.allocations = allocations;
    }

    // Getters and setters
    public UUID getAccountId() {return accountId;}

    public void setAccountId(UUID accountId) {this.accountId = accountId;}

    public UUID getUserId() {return userId;}

    public void setUserId(UUID userId) {this.userId = userId;}

    public BigDecimal getTotalValue() {return totalValue;}

    public void setTotalValue(BigDecimal totalValue) {this.totalValue = totalValue;}

    public BigDecimal getTotalDividends() {return totalDividends;}

    public void setTotalDividends(BigDecimal totalDividends) {this.totalDividends = totalDividends;}

    public int getNumberOfHoldings() {return numberOfHoldings;}

    public void setNumberOfHoldings(int numberOfHoldings) {this.numberOfHoldings = numberOfHoldings;}

    public List<AllocationBreakdownDTO> getAllocations() {return allocations;}

    public void setAllocations(List<AllocationBreakdownDTO> allocations) {this.allocations = allocations;}

}
