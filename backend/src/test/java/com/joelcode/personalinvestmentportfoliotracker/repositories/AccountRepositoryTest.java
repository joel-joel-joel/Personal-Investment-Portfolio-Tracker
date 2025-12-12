package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        entityManager.persistAndFlush(testUser);

        testAccount = new Account();
        testAccount.setAccountName("Checking");
        testAccount.setAccountBalance(BigDecimal.valueOf(5000.0));
        testAccount.setUser(testUser);
        testAccount.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(testAccount);
    }

    @Test
    void testFindByAccountName_Success() {
        // Act
        Optional<Account> result = accountRepository.findByAccountName("Checking");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Checking", result.get().getAccountName());
    }

    @Test
    void testFindByAccountName_NotFound() {
        // Act
        Optional<Account> result = accountRepository.findByAccountName("Nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByAccountId_Success() {
        // Act
        Optional<Account> result = accountRepository.findByAccountId(testAccount.getAccountId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAccount.getAccountId(), result.get().getAccountId());
    }

    @Test
    void testFindByAccountId_NotFound() {
        // Act
        Optional<Account> result = accountRepository.findByAccountId(UUID.randomUUID());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCreatedAtAfter_Success() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusHours(1);

        // Act
        List<Account> results = accountRepository.findByCreatedAtAfter(pastDate);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindByCreatedAtAfter_Empty() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusHours(1);

        // Act
        List<Account> results = accountRepository.findByCreatedAtAfter(futureDate);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindAccountWithinDateRange_Success() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        // Act
        List<Account> results = accountRepository.findAccountWithinDateRange(start, end);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getAccountId().equals(testAccount.getAccountId())));
    }

    @Test
    void testFindAccountWithinDateRange_OutsideRange() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);

        // Act
        List<Account> results = accountRepository.findAccountWithinDateRange(start, end);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByAccountNameContainingIgnoreCase_Success() {
        // Act
        List<Account> results = accountRepository.findByAccountNameContainingIgnoreCase("check");

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getAccountName().equalsIgnoreCase("Checking")));
    }

    @Test
    void testFindByAccountNameContainingIgnoreCase_NotFound() {
        // Act
        List<Account> results = accountRepository.findByAccountNameContainingIgnoreCase("xyz");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByUser_UserIdAndAccountNameContainingIgnoreCase_Success() {
        // Act
        List<Account> results = accountRepository.findByUser_UserIdAndAccountNameContainingIgnoreCase(testUser.getUserId(), "check");

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getUser().getUserId().equals(testUser.getUserId())));
    }

    @Test
    void testFindByAccountBalanceGreaterThan_Success() {
        // Arrange
        BigDecimal threshold = BigDecimal.valueOf(1000.0);

        // Act
        List<Account> results = accountRepository.findByCashBalanceGreaterThan(threshold);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(a -> a.getAccountBalance().compareTo(threshold) > 0));
    }

    @Test
    void testFindByAccountBalanceGreaterThan_Empty() {
        // Arrange
        BigDecimal threshold = BigDecimal.valueOf(10000.0);

        // Act
        List<Account> results = accountRepository.findByCashBalanceGreaterThan(threshold);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindByAccountBalanceLessThan_Success() {
        // Arrange
        BigDecimal threshold = BigDecimal.valueOf(6000.0);

        // Act
        List<Account> results = accountRepository.findByCashBalanceLessThan(threshold);

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a -> a.getAccountBalance().compareTo(threshold) < 0));
    }

    @Test
    void testFindByAccountBalanceLessThan_Empty() {
        // Arrange
        BigDecimal threshold = BigDecimal.valueOf(1000.0);

        // Act
        List<Account> results = accountRepository.findByCashBalanceLessThan(threshold);

        // Assert
        assertTrue(results.isEmpty());
    }
}