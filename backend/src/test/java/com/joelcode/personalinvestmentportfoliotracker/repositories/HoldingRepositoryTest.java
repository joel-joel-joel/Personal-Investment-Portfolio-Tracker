package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.Holding;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import com.joelcode.personalinvestmentportfoliotracker.entities.User;
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
class HoldingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HoldingRepository holdingRepository;

    private Account testAccount;
    private Stock testStock;
    private Holding testHolding;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testuser");
        entityManager.persistAndFlush(user);

        testAccount = new Account();
        testAccount.setAccountName("Test Account");
        testAccount.setUser(user);
        entityManager.persistAndFlush(testAccount);

        testStock = new Stock();
        testStock.setStockCode("AAPL");
        testStock.setCompanyName("Apple");
        entityManager.persistAndFlush(testStock);

        testHolding = new Holding();
        testHolding.setAccount(testAccount);
        testHolding.setStock(testStock);
        testHolding.setQuantity(BigDecimal.valueOf(100));
        testHolding.setAverageCostBasis(BigDecimal.valueOf(150.0));
        testHolding.setTotalCostBasis(BigDecimal.valueOf(15000.0));
        testHolding.setRealizedGain(BigDecimal.valueOf(500.0));
        testHolding.setFirstPurchaseDate(LocalDateTime.now());
        entityManager.persistAndFlush(testHolding);
    }

    @Test
    void testFindByAccount_Success() {
        // Act
        List<Holding> results = holdingRepository.findByAccount(testAccount);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(h -> h.getAccount().getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindByAccountOrderByTotalCostBasisDesc_Success() {
        // Act
        List<Holding> results = holdingRepository.findByAccountOrderByTotalCostBasisDesc(testAccount);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(testHolding.getHoldingId(), results.get(0).getHoldingId());
    }

    @Test
    void testFindAllByAccountId_Success() {
        // Act
        List<Holding> results = holdingRepository.findAllByAccountId(testAccount.getAccountId());

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(h -> h.getHoldingId().equals(testHolding.getHoldingId())));
    }

    @Test
    void testFindByStock_Success() {
        // Act
        List<Holding> results = holdingRepository.findByStock(testStock);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(h -> h.getStock().getStockId().equals(testStock.getStockId())));
    }

    @Test
    void testFindByStock_StockId_Success() {
        // Act
        List<Holding> results = holdingRepository.findByStock_StockId(testStock.getStockId());

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(
                results.stream()
                        .anyMatch(h -> h.getQuantity().compareTo(BigDecimal.valueOf(100)) == 0)
        );
    }

    @Test
    void testFindByAccountAndStock_Success() {
        // Act
        Optional<Holding> result = holdingRepository.findByAccountAndStock(testAccount, testStock);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testHolding.getHoldingId(), result.get().getHoldingId());
    }

    @Test
    void testFindByAccountAndStock_NotFound() {
        // Arrange
        Stock newStock = new Stock();
        newStock.setStockCode("MSFT");
        newStock.setCompanyName("Microsoft");
        entityManager.persistAndFlush(newStock);

        // Act
        Optional<Holding> result = holdingRepository.findByAccountAndStock(testAccount, newStock);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetHoldingByAccountIdAndStockId_Success() {
        // Act
        Optional<Holding> result = holdingRepository.getHoldingByAccount_AccountIdAndStock_StockId(
                testAccount.getAccountId(),
                testStock.getStockId()
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testHolding.getHoldingId(), result.get().getHoldingId());
    }

    @Test
    void testGetHoldingsEntitiesByAccount_Success() {
        // Act
        List<Holding> results = holdingRepository.findByAccount_AccountId(testAccount.getAccountId());

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(h -> h.getAccount().getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindByHoldingId_Success() {
        // Act
        Optional<Holding> result = holdingRepository.findByHoldingId(testHolding.getHoldingId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testHolding.getHoldingId(), result.get().getHoldingId());
    }

    @Test
    void testExistsByAccountAndStock_Success() {
        // Act
        boolean exists = holdingRepository.existsByAccountAndStock(testAccount, testStock);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByAccountAndStock_NotFound() {
        // Arrange
        Stock newStock = new Stock();
        newStock.setStockCode("GOOGL");
        newStock.setCompanyName("Google");
        entityManager.persistAndFlush(newStock);

        // Act
        boolean exists = holdingRepository.existsByAccountAndStock(testAccount, newStock);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testFindByQuantityGreaterThan_Success() {
        // Act
        List<Holding> results = holdingRepository.findByQuantityGreaterThan(BigDecimal.valueOf(50));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(h -> h.getQuantity().compareTo(BigDecimal.valueOf(50)) > 0));
    }

    @Test
    void testFindByAccountAndQuantityGreaterThan_Success() {
        // Act
        List<Holding> results = holdingRepository.findByAccountAndQuantityGreaterThan(testAccount, BigDecimal.valueOf(50));

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(h -> h.getAccount().getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindByFirstPurchaseDateAfter_Success() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // Act
        List<Holding> results = holdingRepository.findByFirstPurchaseDateAfter(pastDate);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(h -> h.getFirstPurchaseDate().isAfter(pastDate)));
    }

    @Test
    void testFindByFirstPurchaseDateBetween_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act
        List<Holding> results = holdingRepository.findByFirstPurchaseDateBetween(start, end);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testSumTotalCostBasisByAccount_Success() {
        // Act
        BigDecimal total = holdingRepository.sumTotalCostBasisByAccount(testAccount);

        // Assert
        assertNotNull(total);
        assertTrue(total.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCountByAccount_Success() {
        // Act
        Long count = holdingRepository.countByAccount(testAccount);

        // Assert
        assertNotNull(count);
        assertTrue(count > 0);
        assertEquals(1L, count);
    }
}