package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.PortfolioSnapshot;
import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PortfolioSnapshotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PortfolioSnapshotRepository portfolioSnapshotRepository;

    private Account testAccount;
    private PortfolioSnapshot testSnapshot;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testuser");
        entityManager.persistAndFlush(user);

        testAccount = new Account();
        testAccount.setAccountName("Test Account");
        testAccount.setUser(user);
        entityManager.persistAndFlush(testAccount);

        testSnapshot = new PortfolioSnapshot();
        testSnapshot.setAccount(testAccount);
        testSnapshot.setSnapshotDate(LocalDate.now());
        testSnapshot.setTotalValue(BigDecimal.valueOf(55000.0));
        testSnapshot.setCashBalance(BigDecimal.valueOf(5000.0));
        testSnapshot.setTotalCostBasis(BigDecimal.valueOf(50000.0));
        testSnapshot.setTotalGain(BigDecimal.valueOf(5000.0));
        testSnapshot.setDayChange(BigDecimal.valueOf(100.0));
        testSnapshot.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testSnapshot);
    }

    @Test
    void testFindByAccount_Success() {
        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByAccount(testAccount);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getAccount().getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindByAccountOrderBySnapshotDateDesc_Success() {
        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByAccountOrderBySnapshotDateDesc(testAccount);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(testSnapshot.getSnapshotId(), results.get(0).getSnapshotId());
    }

    @Test
    void testFindByAccountOrderBySnapshotDateAsc_Success() {
        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByAccountOrderBySnapshotDateAsc(testAccount);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindByAccount_IdOrderBySnapshotDateDesc_Success() {
        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByAccount_AccountIdOrderBySnapshotDateDesc(testAccount.getAccountId());

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getAccount().getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindByAccountAndSnapshotDate_Success() {
        // Act
        Optional<PortfolioSnapshot> result = portfolioSnapshotRepository.findByAccountAndSnapshotDate(testAccount, LocalDate.now());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSnapshot.getSnapshotId(), result.get().getSnapshotId());
    }

    @Test
    void testFindByAccountAndSnapshotDate_NotFound() {
        // Act
        Optional<PortfolioSnapshot> result = portfolioSnapshotRepository.findByAccountAndSnapshotDate(testAccount, LocalDate.now().plusDays(1));

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindBySnapshotDateAfter_Success() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findBySnapshotDateAfter(pastDate);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getSnapshotDate().isAfter(pastDate)));
    }

    @Test
    void testFindBySnapshotDateBetween_Success() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findBySnapshotDateBetween(start, end);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getSnapshotDate().equals(LocalDate.now())));
    }

    @Test
    void testFindByAccountAndSnapshotDateBetween_Success() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByAccountAndSnapshotDateBetween(testAccount, start, end);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(s -> s.getAccount().getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindBySnapshotId_Success() {
        // Act
        Optional<PortfolioSnapshot> result = portfolioSnapshotRepository.findBySnapshotId(testSnapshot.getSnapshotId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSnapshot.getSnapshotId(), result.get().getSnapshotId());
    }

    @Test
    void testExistsByAccountAndSnapshotDate_Success() {
        // Act
        boolean exists = portfolioSnapshotRepository.existsByAccountAndSnapshotDate(testAccount, LocalDate.now());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByAccountAndSnapshotDate_NotFound() {
        // Act
        boolean exists = portfolioSnapshotRepository.existsByAccountAndSnapshotDate(testAccount, LocalDate.now().plusDays(1));

        // Assert
        assertFalse(exists);
    }

    @Test
    void testFindByCreatedAtAfter_Success() {
        // Arrange
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);

        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByCreatedAtAfter(pastDateTime);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getCreatedAt().isAfter(pastDateTime)));
    }

    @Test
    void testFindByCreatedAtBetween_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        // Act
        List<PortfolioSnapshot> results = portfolioSnapshotRepository.findByCreatedAtBetween(start, end);

        // Assert
        assertFalse(results.isEmpty());
    }

    @Test
    void testFindLatestByAccount_Success() {
        // Act
        Optional<PortfolioSnapshot> result = portfolioSnapshotRepository.findLatestByAccount(testAccount);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSnapshot.getSnapshotId(), result.get().getSnapshotId());
    }

    @Test
    void testFindEarliestByAccount_Success() {
        // Act
        Optional<PortfolioSnapshot> result = portfolioSnapshotRepository.findEarliestByAccount(testAccount);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSnapshot.getSnapshotId(), result.get().getSnapshotId());
    }

    @Test
    void testAverageTotalValueByAccountAndDateRange_Success() {
        // Arrange
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        // Act
        BigDecimal average = portfolioSnapshotRepository.averageTotalValueByAccountAndDateRange(testAccount, start, end);

        // Assert
        assertNotNull(average);
        assertTrue(average.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testCountByAccount_Success() {
        // Act
        Long count = portfolioSnapshotRepository.countByAccount(testAccount);

        // Assert
        assertNotNull(count);
        assertTrue(count > 0);
        assertEquals(1L, count);
    }
}