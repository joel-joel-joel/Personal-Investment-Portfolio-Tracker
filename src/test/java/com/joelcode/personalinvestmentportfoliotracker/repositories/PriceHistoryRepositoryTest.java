package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.PriceHistory;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PriceHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    private Stock testStock;
    private PriceHistory testPriceHistory;

    @BeforeEach
    void setUp() {
        testStock = new Stock();
        testStock.setStockCode("AAPL");
        testStock.setCompanyName("Apple");
        entityManager.persistAndFlush(testStock);

        testPriceHistory = new PriceHistory();
        testPriceHistory.setStock(testStock);
        testPriceHistory.setClosePrice(BigDecimal.valueOf(150.0));
        testPriceHistory.setCloseDate(LocalDateTime.now());
        entityManager.persistAndFlush(testPriceHistory);
    }

    @Test
    void testFindByStock_StockId_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_StockId(testStock.getStockId());

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(ph -> ph.getStock().getStockId().equals(testStock.getStockId())));
    }

    @Test
    void testFindByStock_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock(testStock);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(ph -> ph.getClosePrice().compareTo(BigDecimal.valueOf(150.0)) == 0));
    }

    @Test
    void testFindByStock_IdOrderByDateAsc_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_StockIdOrderByCloseDateAsc(testStock.getStockId());

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByStock_IdOrderByDateDesc_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_StockIdOrderByCloseDateDesc(testStock.getStockId());

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(testPriceHistory.getPriceHistoryId(), results.get(0).getPriceHistoryId());
    }

    @Test
    void testFindTopByStock_IdOrderByDateDesc_Success() {
        // Act
        Optional<PriceHistory> result = priceHistoryRepository.findTopByStock_StockIdOrderByCloseDateDesc(testStock.getStockId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(150.0), result.get().getClosePrice());
    }

    @Test
    void testFindByStock_IdAndDateBetween_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_StockIdAndCloseDateBetween(testStock.getStockId(), start, end);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByStock_CompanyName_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_CompanyName("Apple");

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(ph -> ph.getStock().getCompanyName().equals("Apple")));
    }

    @Test
    void testFindByStock_StockCode_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_StockCode("AAPL");

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindLatestPriceByStockId_Success() {
        // Act
        Optional<PriceHistory> result = priceHistoryRepository.findTopByStock_StockIdOrderByCloseDateDesc(testStock.getStockId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(150.0), result.get().getClosePrice());
    }

    @Test
    void testFindByCloseDateGreaterThan_Success() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // Act
        List<PriceHistory> results = priceHistoryRepository.findByCloseDateGreaterThan(pastDate);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(ph -> ph.getCloseDate().isAfter(pastDate)));
    }

    @Test
    void testFindByCloseDateLessThan_Success() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        // Act
        List<PriceHistory> results = priceHistoryRepository.findByCloseDateLessThan(futureDate);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByCloseDateBetween_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act
        List<PriceHistory> results = priceHistoryRepository.findByCloseDateBetween(start, end);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByStock_StockIdAndCloseDateBetween_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act
        List<PriceHistory> results = priceHistoryRepository.findByStock_StockIdAndCloseDateBetween(testStock.getStockId(), start, end);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(ph -> ph.getStock().getStockId().equals(testStock.getStockId())));
    }

    @Test
    void testFindTopByStockOrderByCloseDateDesc_Success() {
        // Act
        Optional<PriceHistory> result = priceHistoryRepository.findTopByStockOrderByCloseDateDesc(testStock);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testPriceHistory.getPriceHistoryId(), result.get().getPriceHistoryId());
    }

    @Test
    void testFindByClosePrice_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByClosePrice(BigDecimal.valueOf(150.0));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(ph -> ph.getClosePrice().compareTo(BigDecimal.valueOf(150.0)) == 0));
    }

    @Test
    void testFindByClosePriceGreaterThan_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByClosePriceGreaterThan(BigDecimal.valueOf(100.0));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(ph -> ph.getClosePrice().compareTo(BigDecimal.valueOf(100.0)) > 0));
    }

    @Test
    void testFindByClosePriceLessThan_Success() {
        // Act
        List<PriceHistory> results = priceHistoryRepository.findByClosePriceLessThan(BigDecimal.valueOf(200.0));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(ph -> ph.getClosePrice().compareTo(BigDecimal.valueOf(200.0)) < 0));
    }
}