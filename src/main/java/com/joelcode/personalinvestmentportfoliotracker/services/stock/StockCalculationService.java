package com.joelcode.personalinvestmentportfoliotracker.services.stock;

import java.util.List;

public interface StockCalculationService {

    double calculateAveragePrice(List<Double> prices);

    double calculatePercentageChange(double oldPrice, double newPrice);

    double calculateMarketValue(double quantity, double currentPrice);

    double calculateUnrealizedGain(double avgBuyPrice, double currentPrice, double quantity);

    double calculateTotalReturnPercent(double avgBuyPrice, double currentPrice);
}
