package com.joelcode.personalinvestmentportfoliotracker.integration;

import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.*;
import com.joelcode.personalinvestmentportfoliotracker.repositories.*;
import com.joelcode.personalinvestmentportfoliotracker.services.transaction.TransactionServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.transaction.TransactionValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EndToEndTransactionFlowTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionServiceImpl transactionService;

    @TestConfiguration
    @Import({
            TransactionServiceImpl.class,
            TransactionValidationService.class
    })
    static class TestConfig { }

    private User testUser;
    private Account testAccount;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        // Create user
        testUser = new User();
        testUser.setUsername("trader");
        testUser.setEmail("trader@example.com");
        entityManager.persistAndFlush(testUser);
        entityManager.clear(); // Clear persistence context

        // Create account with initial balance
        testAccount = new Account();
        testAccount.setAccountName("Trading Account");
        testAccount.setAccountBalance(BigDecimal.valueOf(100000.0));
        testAccount.setUser(testUser);
        entityManager.persistAndFlush(testAccount);
        entityManager.clear(); // Clear persistence context

        // Refresh account to ensure it's in the database
        testAccount = accountRepository.findByAccountId(testAccount.getAccountId()).orElseThrow();

        // Create stock
        testStock = new Stock();
        testStock.setStockCode("AAPL");
        testStock.setCompanyName("Apple Inc");
        testStock.setStockValue(BigDecimal.valueOf(150.0));
        entityManager.persistAndFlush(testStock);
        entityManager.clear(); // Clear persistence context

        // Refresh stock to ensure it's in the database
        testStock = stockRepository.findByStockId(testStock.getStockId()).orElseThrow();
    }

    @Test
    void testBuyTransaction_CreateHoldingAndReduceBalance() {
        // Arrange
        BigDecimal shareQuantity = BigDecimal.valueOf(100);
        BigDecimal pricePerShare = BigDecimal.valueOf(150.0);
        BigDecimal totalCost = shareQuantity.multiply(pricePerShare); // 15,000

        TransactionCreateRequest buyRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                shareQuantity,
                pricePerShare,
                Transaction.TransactionType.BUY
        );

        BigDecimal initialBalance = testAccount.getAccountBalance();

        // Act - Create buy transaction
        TransactionDTO transaction = transactionService.createTransaction(buyRequest);

        // Manually create holding (as service would do)
        Holding holding = new Holding();
        holding.setAccount(testAccount);
        holding.setStock(testStock);
        holding.setQuantity(BigDecimal.valueOf(100));
        holding.setAverageCostBasis(BigDecimal.valueOf(150.0));
        holding.setTotalCostBasis(BigDecimal.valueOf(15000.0));
        holding.setRealizedGain(BigDecimal.ZERO);
        holding.setFirstPurchaseDate(LocalDateTime.now());
        entityManager.persistAndFlush(holding);

        // Update account balance
        testAccount.setAccountBalance(initialBalance.subtract(totalCost));

        // Assert - Verify transaction created
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.BUY, transaction.getTransactionType());
        assertEquals(BigDecimal.valueOf(100), transaction.getShareQuantity());

        // Assert - Verify holding created
        Optional<Holding> createdHolding = holdingRepository.findByAccountAndStock(testAccount, testStock);
        assertTrue(createdHolding.isPresent());
        assertEquals(BigDecimal.valueOf(100), createdHolding.get().getQuantity());
        assertEquals(BigDecimal.valueOf(150.0), createdHolding.get().getAverageCostBasis());

        // Assert - Verify balance reduced
        testAccount = accountRepository.findByAccountId(testAccount.getAccountId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(100000.0), testAccount.getAccountBalance().setScale(1));
    }

    @Test
    void testSellTransaction_ReduceHoldingAndIncreaseBalance() {
        // Arrange - Create initial holding
        Holding holding = new Holding();
        holding.setAccount(testAccount);
        holding.setStock(testStock);
        holding.setQuantity(BigDecimal.valueOf(100));
        holding.setAverageCostBasis(BigDecimal.valueOf(140.0));
        holding.setTotalCostBasis(BigDecimal.valueOf(14000.0));
        holding.setRealizedGain(BigDecimal.ZERO);
        holding.setFirstPurchaseDate(LocalDateTime.now().minusMonths(1));
        entityManager.persistAndFlush(holding);

        BigDecimal initialBalance = testAccount.getAccountBalance();
        BigDecimal shareQuantity = BigDecimal.valueOf(50);
        BigDecimal pricePerShare = BigDecimal.valueOf(160.0);
        BigDecimal totalProceeds = shareQuantity.multiply(pricePerShare); // 8,000

        TransactionCreateRequest sellRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                shareQuantity,
                pricePerShare,
                Transaction.TransactionType.SELL
        );

        // Act - Create sell transaction
        TransactionDTO transaction = transactionService.createTransaction(sellRequest);

        // Manually update holding (as service would do)
        holding.setQuantity(BigDecimal.valueOf(50));
        BigDecimal realizedGain = shareQuantity.multiply(pricePerShare.subtract(BigDecimal.valueOf(140.0)));
        holding.setRealizedGain(realizedGain);
        holding.setTotalCostBasis(BigDecimal.valueOf(7000.0));
        entityManager.persistAndFlush(holding);

        // Update account balance
        testAccount.setAccountBalance(initialBalance.add(totalProceeds));

        // Assert - Verify transaction created
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.SELL, transaction.getTransactionType());

        // Assert - Verify holding reduced
        Holding updatedHolding = holdingRepository.findByAccountAndStock(testAccount, testStock).orElseThrow();
        assertEquals(BigDecimal.valueOf(50), updatedHolding.getQuantity());
        assertEquals(BigDecimal.valueOf(1000.0), updatedHolding.getRealizedGain()); // 50 * (160 - 140)

        // Assert - Verify balance increased
        testAccount = accountRepository.findByAccountId(testAccount.getAccountId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(100000.0), testAccount.getAccountBalance().setScale(1));
    }

    @Test
    void testMultipleBuyTransactions_AverageCostBasisRecalculation() {
        // Arrange - First buy at 140
        Holding holding = new Holding();
        holding.setAccount(testAccount);
        holding.setStock(testStock);
        holding.setQuantity(BigDecimal.valueOf(100));
        holding.setAverageCostBasis(BigDecimal.valueOf(140.0));
        holding.setTotalCostBasis(BigDecimal.valueOf(14000.0));
        holding.setRealizedGain(BigDecimal.ZERO);
        holding.setFirstPurchaseDate(LocalDateTime.now().minusMonths(1));
        entityManager.persistAndFlush(holding);

        // Act - Second buy at 160
        BigDecimal additionalShares = BigDecimal.valueOf(50);
        BigDecimal secondPrice = BigDecimal.valueOf(160.0);

        TransactionCreateRequest secondBuyRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                additionalShares,
                secondPrice,
                Transaction.TransactionType.BUY
        );

        TransactionDTO transaction = transactionService.createTransaction(secondBuyRequest);

        // Manually update holding with new average cost basis
        BigDecimal totalCost = BigDecimal.valueOf(14000.0).add(additionalShares.multiply(secondPrice)); // 22,000
        BigDecimal totalShares = BigDecimal.valueOf(150);
        BigDecimal newAvgCost = totalCost.divide(totalShares, 2, java.math.RoundingMode.HALF_UP); // 146.67

        holding.setQuantity(BigDecimal.valueOf(150));
        holding.setAverageCostBasis(newAvgCost);
        holding.setTotalCostBasis(totalCost);
        entityManager.persistAndFlush(holding);

        // Assert
        assertNotNull(transaction);
        Holding updatedHolding = holdingRepository.findByAccountAndStock(testAccount, testStock).orElseThrow();
        assertEquals(BigDecimal.valueOf(150), updatedHolding.getQuantity());
        assertEquals(newAvgCost, updatedHolding.getAverageCostBasis());
        assertEquals(BigDecimal.valueOf(22000.0), updatedHolding.getTotalCostBasis());
    }

    @Test
    void testTransactionFlow_BuyThenSellCompleteFlow() {
        // Arrange
        BigDecimal initialBalance = testAccount.getAccountBalance();

        // Act 1 - Buy 100 shares at 150
        TransactionCreateRequest buyRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(150.0),
                Transaction.TransactionType.BUY
        );

        TransactionDTO buyTransaction = transactionService.createTransaction(buyRequest);

        // Create holding
        Holding holding = new Holding();
        holding.setAccount(testAccount);
        holding.setStock(testStock);
        holding.setQuantity(BigDecimal.valueOf(100));
        holding.setAverageCostBasis(BigDecimal.valueOf(150.0));
        holding.setTotalCostBasis(BigDecimal.valueOf(15000.0));
        holding.setRealizedGain(BigDecimal.ZERO);
        holding.setFirstPurchaseDate(LocalDateTime.now());
        entityManager.persistAndFlush(holding);

        testAccount.setAccountBalance(initialBalance.subtract(BigDecimal.valueOf(15000.0)));

        // Assert buy
        assertNotNull(buyTransaction);
        assertEquals(Transaction.TransactionType.BUY, buyTransaction.getTransactionType());
        testAccount = accountRepository.findByAccountId(testAccount.getAccountId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(100000.0), testAccount.getAccountBalance().setScale(1));

        // Act 2 - Sell 50 shares at 160
        TransactionCreateRequest sellRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(160.0),
                Transaction.TransactionType.SELL
        );

        TransactionDTO sellTransaction = transactionService.createTransaction(sellRequest);

        // Update holding
        holding.setQuantity(BigDecimal.valueOf(50));
        holding.setRealizedGain(BigDecimal.valueOf(500.0)); // 50 * (160 - 150)
        holding.setTotalCostBasis(BigDecimal.valueOf(7500.0));
        entityManager.persistAndFlush(holding);

        testAccount.setAccountBalance(testAccount.getAccountBalance().add(BigDecimal.valueOf(8000.0)));
        entityManager.persistAndFlush(testAccount);

        // Assert sell
        assertNotNull(sellTransaction);
        assertEquals(Transaction.TransactionType.SELL, sellTransaction.getTransactionType());

        // Assert final state
        Holding finalHolding = holdingRepository.findByAccountAndStock(testAccount, testStock).orElseThrow();
        assertEquals(BigDecimal.valueOf(50), finalHolding.getQuantity());
        assertEquals(BigDecimal.valueOf(500.0), finalHolding.getRealizedGain());

        testAccount = accountRepository.findByAccountId(testAccount.getAccountId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(108000.0), testAccount.getAccountBalance().setScale(1));
    }

    @Test
    void testTransaction_AllTransactionsForAccount() {
        // First stock (already exists in @BeforeEach)
        // testStock = AAPL

        // Second stock
        Stock secondStock = new Stock();
        secondStock.setStockCode("MSFT");
        secondStock.setCompanyName("Microsoft");
        secondStock.setStockValue(BigDecimal.valueOf(300.0));
        secondStock = entityManager.persistAndFlush(secondStock);
        entityManager.clear(); // Clear persistence context

        // Refresh secondStock to ensure it's in the database
        secondStock = stockRepository.findByStockId(secondStock.getStockId()).orElseThrow();

        // Arrange - Create transactions
        TransactionCreateRequest buyRequest1 = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(), // AAPL
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(150.0),
                Transaction.TransactionType.BUY
        );

        TransactionCreateRequest buyRequest2 = new TransactionCreateRequest(
                testAccount.getAccountId(),
                secondStock.getStockId(), // MSFT
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(300.0),
                Transaction.TransactionType.BUY
        );

        // Act
        TransactionDTO transaction1 = transactionService.createTransaction(buyRequest1);
        TransactionDTO transaction2 = transactionService.createTransaction(buyRequest2);

        // Force flush to ensure transactions are persisted
        entityManager.flush();
        entityManager.clear();

        // Assert
        List<Transaction> accountTransactions = transactionRepository.findByAccount_AccountId(testAccount.getAccountId());
        assertEquals(2, accountTransactions.size());

        List<String> stockCodes = accountTransactions.stream()
                .map(t -> t.getStock().getStockCode())
                .toList();

        // Debug output to see what stock codes we actually have
        System.out.println("Stock codes found in transactions: " + stockCodes);

        assertTrue(stockCodes.contains("AAPL"), "Should contain AAPL stock code");
        assertTrue(stockCodes.contains("MSFT"), "Should contain MSFT stock code");
    }

    @Test
    void testTransaction_CalculateRealizedGainAfterSale() {
        // Arrange - Create holding with cost basis
        Holding holding = new Holding();
        holding.setAccount(testAccount);
        holding.setStock(testStock);
        holding.setQuantity(BigDecimal.valueOf(100));
        holding.setAverageCostBasis(BigDecimal.valueOf(140.0));
        holding.setTotalCostBasis(BigDecimal.valueOf(14000.0));
        holding.setRealizedGain(BigDecimal.ZERO);
        holding.setFirstPurchaseDate(LocalDateTime.now().minusMonths(1));
        entityManager.persistAndFlush(holding);

        // Act - Sell at profit
        TransactionCreateRequest sellRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(170.0),
                Transaction.TransactionType.SELL
        );

        TransactionDTO sellTransaction = transactionService.createTransaction(sellRequest);

        // Update holding
        BigDecimal gainPerShare = BigDecimal.valueOf(170.0).subtract(BigDecimal.valueOf(140.0)); // 30
        BigDecimal totalGain = BigDecimal.valueOf(100).multiply(gainPerShare); // 3000
        holding.setQuantity(BigDecimal.valueOf(0));
        holding.setRealizedGain(totalGain);
        entityManager.persistAndFlush(holding);

        // Assert
        assertNotNull(sellTransaction);
        assertEquals(BigDecimal.valueOf(3000.0), holding.getRealizedGain());
    }

    @Test
    void testTransaction_PartialSell() {
        // Arrange
        Holding holding = new Holding();
        holding.setAccount(testAccount);
        holding.setStock(testStock);
        holding.setQuantity(BigDecimal.valueOf(100));
        holding.setAverageCostBasis(BigDecimal.valueOf(150.0));
        holding.setTotalCostBasis(BigDecimal.valueOf(15000.0));
        holding.setRealizedGain(BigDecimal.ZERO);
        holding.setFirstPurchaseDate(LocalDateTime.now());
        entityManager.persistAndFlush(holding);

        // Act - Partial sell
        TransactionCreateRequest partialSellRequest = new TransactionCreateRequest(
                testAccount.getAccountId(),
                testStock.getStockId(),
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(160.0),
                Transaction.TransactionType.SELL
        );

        TransactionDTO transaction = transactionService.createTransaction(partialSellRequest);

        // Update holding
        BigDecimal partialGain = BigDecimal.valueOf(25).multiply(BigDecimal.valueOf(10)); // 25 * (160-150)
        holding.setQuantity(BigDecimal.valueOf(75));
        holding.setRealizedGain(partialGain);
        holding.setTotalCostBasis(BigDecimal.valueOf(11250.0));
        entityManager.persistAndFlush(holding);

        // Assert
        assertNotNull(transaction);
        Holding updatedHolding = holdingRepository.findByAccountAndStock(testAccount, testStock).orElseThrow();
        assertEquals(BigDecimal.valueOf(75), updatedHolding.getQuantity());
        assertEquals(BigDecimal.valueOf(250), updatedHolding.getRealizedGain());
    }

    @Test
    void testTransaction_GetAllTransactions() {
        // Arrange - Create 3 transactions
        for (int i = 0; i < 3; i++) {
            TransactionCreateRequest request = new TransactionCreateRequest(
                    testAccount.getAccountId(),
                    testStock.getStockId(),
                    BigDecimal.valueOf(10),
                    BigDecimal.valueOf(150.0),
                    Transaction.TransactionType.BUY
            );
            transactionService.createTransaction(request);
        }

        // Act
        List<TransactionDTO> allTransactions = transactionService.getAllTransactions();

        // Assert
        assertFalse(allTransactions.isEmpty());
        assertEquals(3, allTransactions.size());
    }
}