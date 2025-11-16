package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.PriceHistory;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {

    // Find price history
    List<PriceHistory> findByStock_StockId(UUID stockId);

    List<PriceHistory> findByStock_CompanyName(String companyName);

    List<PriceHistory> findByStock_StockCode(String stockCode);

    // Filter by closing date
    List<PriceHistory> findByCloseDateGreaterThan(LocalDateTime closeDate);

    List<PriceHistory> findByCloseDateLessThan(LocalDateTime closeDate);

    List<PriceHistory> findByCloseDateBetween(LocalDateTime start, LocalDateTime end);

    List<PriceHistory> findByStock_StockIdAndCloseDateBetween(UUID stockId, LocalDateTime start, LocalDateTime end);

    Optional<PriceHistory> findTopByStockOrderByDateDesc(Stock stock);
    // Filter by closing price
    List<PriceHistory> findByClosePrice(BigDecimal closePrice);

    List<PriceHistory> findByClosePriceGreaterThan(BigDecimal closePrice);

    List<PriceHistory> findByClosePriceLessThan(BigDecimal closePrice);
}
