package com.joelcode.personalinvestmentportfoliotracker.services.stock;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockCalculationServiceImpl implements StockCalculationService{

    // For common calculation operations

    @Override
    public double calculateAveragePrice(List<Double> prices) {
        if (prices == null || prices.isEmpty()) return 0.0;
        double sum = prices.stream().mapToDouble(Double::doubleValue).sum();
        return sum / prices.size();
    }

    @Override
    public double calculatePercentageChange(double oldPrice, double newPrice) {
        if (oldPrice == 0) return 0;
        return (newPrice - oldPrice) / oldPrice * 100;
    }

    @Override
    public double calculateMarketValue(double quantity, double currentPrice) {
        return quantity * currentPrice;
    }

    @Override
    public double calculateUnrealizedGain(double avgBuyPrice, double currentPrice, double quantity) {
        return currentPrice - avgBuyPrice * quantity;
    }

    @Override
    public double calculateTotalReturnPercent(double avgBuyPrice, double currentPrice) {
        if (avgBuyPrice == 0) return 0;
        return calculatePercentageChange(avgBuyPrice, currentPrice);
    }
}
