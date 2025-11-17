package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.aggregation;

import com.joelcode.personalinvestmentportfoliotracker.dto.model.AllocationBreakdownDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.model.PortfolioOverviewDTO;

import java.util.List;
import java.util.UUID;

public interface PortfolioAggregationService {

    // Aggregate the full portfolio overview for an account. Inclusive of holdings, cash, dividends, un/realized gains
    PortfolioOverviewDTO getPortfolioOverview(UUID accountId);

    List<AllocationBreakdownDTO > getAllocationBreakdown(UUID accountId);

}
