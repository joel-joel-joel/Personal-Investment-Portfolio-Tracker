package com.joelcode.personalinvestmentportfoliotracker.services.holding;

import com.joelcode.personalinvestmentportfoliotracker.entities.Holding;

import java.math.BigDecimal;
import java.util.UUID;

public interface HoldingCalculationService {

    BigDecimal calculateTotalPortfolioValue(UUID accountId);

    BigDecimal calculateCurrentValue(Holding holding);

    BigDecimal calculateTotalCostBasis(UUID accountId);

    BigDecimal calculateTotalUnrealizedGain(UUID accountId);

    BigDecimal calculateTotalRealizedGain(UUID accountId);
}