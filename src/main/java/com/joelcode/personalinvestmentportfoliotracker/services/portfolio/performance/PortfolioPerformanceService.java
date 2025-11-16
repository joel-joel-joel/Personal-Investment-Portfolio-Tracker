package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.performance;

import com.joelcode.personalinvestmentportfoliotracker.dto.model.PortfolioPerformanceDTO;

import java.util.UUID;

public interface PortfolioPerformanceService {

    PortfolioPerformanceDTO calculatePortfolioPerformance(UUID accountId);

    void createPortfolioSnapshot(UUID accountId);
}
