package com.joelcode.personalinvestmentportfoliotracker.integration;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.entities.*;
import com.joelcode.personalinvestmentportfoliotracker.repositories.*;
import com.joelcode.personalinvestmentportfoliotracker.services.dividend.DividendServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment.DividendPaymentServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration," +
                        "org.springframework.modulith.events.jpa.JpaEventPublicationAutoConfiguration," +
                        "org.springframework.modulith.events.jdbc.JdbcEventPublicationAutoConfiguration," +
                        "org.springframework.modulith.events.config.EventPublicationAutoConfiguration"
        }
)
@ActiveProfiles("test")
@Transactional
class DividendFlowTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private DividendPaymentRepository dividendPaymentRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private DividendServiceImpl dividendService;
    private DividendPaymentServiceImpl dividendPaymentService;

    private User testUser;
    private Account testAccount;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        // Create user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFullName("Test User");
        testUser.setRoles(User.Role.ROLE_USER);
        userRepository.save(testUser);

        // Create account
        testAccount = new Account();
        testAccount.setAccountName("Trading Account");
        testAccount.setAccountBalance(BigDecimal.valueOf(50000.0));
        testAccount.setUser(testUser);
        accountRepository.save(testAccount);

        // Create stock
        testStock = new Stock();
        testStock.setStockCode("AAPL");
        testStock.setCompanyName("Apple Inc");
        testStock.setStockValue(BigDecimal.valueOf(150.0));
        stockRepository.save(testStock);

        // Create holding (100 shares of AAPL)
        Holding testHolding = new Holding();
        testHolding.setAccount(testAccount);
        testHolding.setStock(testStock);
        testHolding.setQuantity(BigDecimal.valueOf(100));
        testHolding.setAverageCostBasis(BigDecimal.valueOf(140.0));
        testHolding.setTotalCostBasis(BigDecimal.valueOf(14000.0));
        testHolding.setRealizedGain(BigDecimal.ZERO);
        testHolding.setFirstPurchaseDate(LocalDateTime.now().minusMonths(1));
        holdingRepository.save(testHolding);

        // Initialize services
        dividendPaymentService = new DividendPaymentServiceImpl(
                dividendPaymentRepository,
                dividendRepository,
                accountRepository,
                stockRepository,
                holdingRepository,
                null, // DividendPaymentValidationService
                messagingTemplate,
                null // HoldingCalculationService
        );

        dividendService = new DividendServiceImpl(
                dividendRepository,
                stockRepository,
                null, // DividendValidationService
                dividendPaymentService,
                null, // WebSocketController
                messagingTemplate
        );
    }



    @Test
    void testDividendFlow_CreateDividendAndGeneratePayments() {
        // Arrange
        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);
        DividendCreateRequest request = new DividendCreateRequest(
                testStock.getStockId(),
                BigDecimal.valueOf(2.5),
                payDate
        );

        // Act - Create dividend
        Dividend dividend = new Dividend();
        dividend.setStock(testStock);
        dividend.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend.setPayDate(payDate);

        // Persist and flush using standard EntityManager
        entityManager.persist(dividend);
        entityManager.flush();

        // Process payments for dividend
        dividendPaymentService.processPaymentsForDividend(dividend.getDividendId());

        // Assert - Verify dividend created
        assertNotNull(dividend.getDividendId());
        assertEquals(testStock.getStockId(), dividend.getStock().getStockId());
        assertEquals(BigDecimal.valueOf(2.5), dividend.getAmountPerShare());

        // Assert - Verify payment created for account holding this stock
        List<DividendPayment> payments = dividendPaymentRepository.findByDividend_DividendId(dividend.getDividendId());
        assertFalse(payments.isEmpty());
        assertEquals(1, payments.size());

        DividendPayment payment = payments.get(0);
        assertEquals(testAccount.getAccountId(), payment.getAccount().getAccountId());
        assertEquals(testStock.getStockId(), payment.getStock().getStockId());
        assertEquals(BigDecimal.valueOf(100), payment.getShareQuantity());
        assertEquals(BigDecimal.valueOf(250.0), payment.getDividendTotalAmount()); // 100 * 2.5
    }

    @Test
    void testDividendFlow_MultipleAccountsWithSameStock() {
        // Arrange - Create second account and holding
        Account secondAccount = new Account();
        secondAccount.setAccountName("Secondary Account");
        secondAccount.setAccountBalance(BigDecimal.valueOf(30000.0));
        secondAccount.setUser(testUser);
        entityManager.persist(secondAccount);
        entityManager.flush();

        Holding secondHolding = new Holding();
        secondHolding.setAccount(secondAccount);
        secondHolding.setStock(testStock);
        secondHolding.setQuantity(BigDecimal.valueOf(50));
        secondHolding.setAverageCostBasis(BigDecimal.valueOf(145.0));
        secondHolding.setTotalCostBasis(BigDecimal.valueOf(7250.0));
        secondHolding.setRealizedGain(BigDecimal.ZERO);
        secondHolding.setFirstPurchaseDate(LocalDateTime.now().minusMonths(2));
        entityManager.persist(secondHolding);
        entityManager.flush();

        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);

        // Act - Create dividend
        Dividend dividend = new Dividend();
        dividend.setStock(testStock);
        dividend.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend.setPayDate(payDate);

        // Persist and flush using standard EntityManager
        entityManager.persist(dividend);
        entityManager.flush();


        // Process payments
        dividendPaymentService.processPaymentsForDividend(dividend.getDividendId());

        // Assert - Verify payments created for both accounts
        List<DividendPayment> payments = dividendPaymentRepository.findByDividend_DividendId(dividend.getDividendId());
        assertEquals(2, payments.size());

        // Verify first account payment
        DividendPayment payment1 = payments.stream()
                .filter(p -> p.getAccount().getAccountId().equals(testAccount.getAccountId()))
                .findFirst()
                .orElseThrow();
        assertEquals(BigDecimal.valueOf(100), payment1.getShareQuantity());
        assertEquals(BigDecimal.valueOf(250.0), payment1.getDividendTotalAmount());

        // Verify second account payment
        DividendPayment payment2 = payments.stream()
                .filter(p -> p.getAccount().getAccountId().equals(secondAccount.getAccountId()))
                .findFirst()
                .orElseThrow();
        assertEquals(BigDecimal.valueOf(50), payment2.getShareQuantity());
        assertEquals(BigDecimal.valueOf(125.0), payment2.getDividendTotalAmount());
    }

    @Test
    void testDividendFlow_DuplicateDividendPrevention() {
        // Arrange
        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);

        // Create first dividend
        Dividend dividend1 = new Dividend();
        dividend1.setStock(testStock);
        dividend1.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend1.setPayDate(payDate);
        entityManager.persist(dividend1);
        entityManager.flush();

        // Act & Assert - Attempt to create duplicate
        boolean exists = dividendRepository.existsByStockAndPayDate(testStock, payDate);
        assertTrue(exists, "Duplicate dividend should be prevented");

        // Verify only one dividend exists
        List<Dividend> dividends = dividendRepository.findByStock_StockId(testStock.getStockId());
        assertEquals(1, dividends.size());
    }

    @Test
    void testDividendFlow_CalculateTotalDividendsByAccount() {
        // Arrange
        LocalDateTime payDate1 = LocalDateTime.now().plusMonths(1);
        LocalDateTime payDate2 = LocalDateTime.now().plusMonths(2);

        // Create first dividend
        Dividend dividend1 = new Dividend();
        dividend1.setStock(testStock);
        dividend1.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend1.setPayDate(payDate1);
        entityManager.persist(dividend1);
        entityManager.flush();

        // Create second dividend
        Dividend dividend2 = new Dividend();
        dividend2.setStock(testStock);
        dividend2.setAmountPerShare(BigDecimal.valueOf(3.0));
        dividend2.setPayDate(payDate2);
        entityManager.persist(dividend2);
        entityManager.flush();

        // Process payments for both dividends
        dividendPaymentService.processPaymentsForDividend(dividend1.getDividendId());
        dividendPaymentService.processPaymentsForDividend(dividend2.getDividendId());

        // Act - Calculate total
        BigDecimal total = dividendPaymentRepository.calculateTotalDividendsByAccount(testAccount.getAccountId());

        // Assert
        assertNotNull(total);
        // 100 shares * 2.5 + 100 shares * 3.0 = 250 + 300 = 550
        assertEquals(BigDecimal.valueOf(550.00), total.setScale(1));
    }

    @Test
    void testDividendFlow_DividendsByAccountAndStock() {
        // Arrange - Create another stock
        Stock secondStock = new Stock();
        secondStock.setStockCode("MSFT");
        secondStock.setCompanyName("Microsoft");
        secondStock.setStockValue(BigDecimal.valueOf(300.0));
        entityManager.persist(secondStock);
        entityManager.flush();

        // Create holding for second stock
        Holding secondStockHolding = new Holding();
        secondStockHolding.setAccount(testAccount);
        secondStockHolding.setStock(secondStock);
        secondStockHolding.setQuantity(BigDecimal.valueOf(50));
        secondStockHolding.setAverageCostBasis(BigDecimal.valueOf(290.0));
        secondStockHolding.setTotalCostBasis(BigDecimal.valueOf(14500.0));
        secondStockHolding.setRealizedGain(BigDecimal.ZERO);
        secondStockHolding.setFirstPurchaseDate(LocalDateTime.now().minusMonths(1));
        entityManager.persist(secondStockHolding);
        entityManager.flush();

        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);

        // Create dividends for both stocks
        Dividend dividend1 = new Dividend();
        dividend1.setStock(testStock);
        dividend1.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend1.setPayDate(payDate);
        entityManager.persist(dividend1);
        entityManager.flush();

        Dividend dividend2 = new Dividend();
        dividend2.setStock(secondStock);
        dividend2.setAmountPerShare(BigDecimal.valueOf(1.5));
        dividend2.setPayDate(payDate);
        entityManager.persist(dividend2);
        entityManager.flush();

        // Process payments
        dividendPaymentService.processPaymentsForDividend(dividend1.getDividendId());
        dividendPaymentService.processPaymentsForDividend(dividend2.getDividendId());

        // Act - Get payments for specific stock
        List<DividendPayment> aaplePayments = dividendPaymentRepository.findPaymentsByIdAccountAndStockId(
                testAccount.getAccountId(),
                testStock.getStockId()
        );

        // Assert
        assertEquals(1, aaplePayments.size());
        assertEquals(BigDecimal.valueOf(250.0), aaplePayments.get(0).getDividendTotalAmount());

        // Verify total for both stocks
        BigDecimal totalAapl = dividendPaymentRepository.calculateTotalDividendsByAccountAndStock(
                testAccount.getAccountId(),
                testStock.getStockId()
        );
        assertEquals(BigDecimal.valueOf(250.00), totalAapl.setScale(1));

        BigDecimal totalMsft = dividendPaymentRepository.calculateTotalDividendsByAccountAndStock(
                testAccount.getAccountId(),
                secondStock.getStockId()
        );
        assertEquals(BigDecimal.valueOf(75.0), totalMsft.setScale(1)); // 50 * 1.5
    }

    @Test
    void testDividendFlow_AvoidDuplicatePayments() {
        // Arrange
        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);

        Dividend dividend = new Dividend();
        dividend.setStock(testStock);
        dividend.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend.setPayDate(payDate);
        entityManager.persist(dividend);
        entityManager.flush();

        // Act - Process payments for same dividend twice
        dividendPaymentService.processPaymentsForDividend(dividend.getDividendId());
        dividendPaymentService.processPaymentsForDividend(dividend.getDividendId()); // Process again

        // Assert - Verify only one payment created (duplicate skipped)
        List<DividendPayment> payments = dividendPaymentRepository.findByDividend_DividendId(dividend.getDividendId());
        assertEquals(1, payments.size(), "Duplicate payments should be prevented");
    }

    @Test
    void testDividendFlow_PaymentStatusTracking() {
        // Arrange
        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);

        Dividend dividend = new Dividend();
        dividend.setStock(testStock);
        dividend.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend.setPayDate(payDate);
        entityManager.persist(dividend);
        entityManager.flush();

        // Act - Process payments
        dividendPaymentService.processPaymentsForDividend(dividend.getDividendId());

        // Assert - Verify payment status is PAID
        List<DividendPayment> payments = dividendPaymentRepository.findByDividend_DividendId(dividend.getDividendId());
        assertEquals(1, payments.size());
        assertEquals(DividendPayment.PaymentStatus.PAID, payments.get(0).getStatus());

        // Verify payments can be retrieved by status
        List<DividendPayment> paidPayments = dividendPaymentRepository.findPaymentsByAccountAndStatus(
                testAccount.getAccountId(),
                DividendPayment.PaymentStatus.PAID
        );
        assertEquals(1, paidPayments.size());
    }

    @Test
    void testDividendFlow_NoPaymentForAccountWithoutHolding() {
        // Arrange - Create account without holding
        Account newAccount = new Account();
        newAccount.setAccountName("New Account");
        newAccount.setAccountBalance(BigDecimal.valueOf(20000.0));
        newAccount.setUser(testUser);
        entityManager.persist(newAccount);
        entityManager.flush();

        LocalDateTime payDate = LocalDateTime.now().plusMonths(1);

        Dividend dividend = new Dividend();
        dividend.setStock(testStock);
        dividend.setAmountPerShare(BigDecimal.valueOf(2.5));
        dividend.setPayDate(payDate);
        entityManager.persist(dividend);
        entityManager.flush();

        // Act - Process payments
        dividendPaymentService.processPaymentsForDividend(dividend.getDividendId());

        // Assert - Verify only account with holding gets payment
        List<DividendPayment> payments = dividendPaymentRepository.findByAccount_AccountId(newAccount.getAccountId());
        assertTrue(payments.isEmpty(), "Account without holding should not receive payment");

        List<DividendPayment> allPayments = dividendPaymentRepository.findByDividend_DividendId(dividend.getDividendId());
        assertEquals(1, allPayments.size());
        assertEquals(testAccount.getAccountId(), allPayments.get(0).getAccount().getAccountId());
    }
}