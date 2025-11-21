package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.Dividend;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DividendRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DividendRepository dividendRepository;

    private Stock testStock;
    private Dividend testDividend;

    @BeforeEach
    void setUp() {
        testStock = new Stock();
        testStock.setStockCode("AAPL");
        testStock.setCompanyName("Apple Inc");
        entityManager.persistAndFlush(testStock);

        testDividend = new Dividend();
        testDividend.setDividendAmountPerShare(BigDecimal.valueOf(2.5));
        testDividend.setPayDate(LocalDateTime.now());
        testDividend.setStock(testStock);
        entityManager.persistAndFlush(testDividend);
    }

    @Test
    void testFindByStock_Success() {
        // Act
        List<Dividend> results = dividendRepository.findByStock(testStock);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(d -> d.getStock().getStockId().equals(testStock.getStockId())));
    }

    @Test
    void testFindByStock_StockId_Success() {
        // Act
        List<Dividend> results = dividendRepository.findByStock_StockId(testStock.getStockId());

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(
                results.stream()
                        .anyMatch(d -> d.getDividendAmountPerShare().compareTo(BigDecimal.valueOf(2.5)) == 0)
        );    }

    @Test
    void testFindByStock_StockCode_Success() {
        // Act
        List<Dividend> results = dividendRepository.findByStock_StockCode("AAPL");

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(d -> d.getStock().getStockCode().equals("AAPL")));
    }

    @Test
    void testFindByStock_StockCode_NotFound() {
        // Act
        List<Dividend> results = dividendRepository.findByStock_StockCode("MSFT");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testExistsByStockAndPayDate_Success() {
        // Act
        boolean exists = dividendRepository.existsByStockAndPayDate(testStock, testDividend.getPayDate());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByStockAndPayDate_NotFound() {
        // Arrange
        LocalDateTime differentDate = LocalDateTime.now().plusDays(1);

        // Act
        boolean exists = dividendRepository.existsByStockAndPayDate(testStock, differentDate);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testFindByDividendPerShareGreaterThan_Success() {
        // Act
        List<Dividend> results = dividendRepository.findByDividendPerShareGreaterThan(BigDecimal.valueOf(2.0));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(d -> d.getDividendAmountPerShare().compareTo(BigDecimal.valueOf(2.0)) > 0));
    }

    @Test
    void testFindByDividendPerShareGreaterThan_Empty() {
        // Act
        List<Dividend> results = dividendRepository.findByDividendPerShareGreaterThan(BigDecimal.valueOf(10.0));

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByDividendPerShareLessThan_Success() {
        // Act
        List<Dividend> results = dividendRepository.findByDividendPerShareLessThan(BigDecimal.valueOf(3.0));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(d -> d.getDividendAmountPerShare().compareTo(BigDecimal.valueOf(3.0)) < 0));
    }

    @Test
    void testFindByPayDate_Success() {
        // Act
        List<Dividend> results = dividendRepository.findByPayDate(testDividend.getPayDate());

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByPayDateAfter_Success() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // Act
        List<Dividend> results = dividendRepository.findByPayDateAfter(pastDate);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(d -> d.getPayDate().isAfter(pastDate)));
    }

    @Test
    void testFindByPayDateBefore_Success() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        // Act
        List<Dividend> results = dividendRepository.findByPayDateBefore(futureDate);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByPayDateBetween_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act
        List<Dividend> results = dividendRepository.findByPayDateBetween(start, end);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(d -> d.getPayDate().isAfter(start) && d.getPayDate().isBefore(end)));
    }

    @Test
    void testFindUpcomingDividends_Success() {
        // Act
        List<Dividend> results = dividendRepository.findUpcomingDividends(LocalDateTime.now().minusHours(1));

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindUpcomingDividends_Empty() {
        // Act
        List<Dividend> results = dividendRepository.findUpcomingDividends(LocalDateTime.now().plusDays(1));

        // Assert
        assertTrue(results.isEmpty());
    }
}