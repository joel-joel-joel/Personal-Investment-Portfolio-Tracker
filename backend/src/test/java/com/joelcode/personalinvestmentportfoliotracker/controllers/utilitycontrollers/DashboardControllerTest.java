package com.joelcode.personalinvestmentportfoliotracker.controllers.utilitycontrollers;

import com.joelcode.personalinvestmentportfoliotracker.dto.holding.HoldingDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.utility.DashboardDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.PortfolioOverviewDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.PortfolioPerformanceDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.Transaction;
import com.joelcode.personalinvestmentportfoliotracker.repositories.AccountRepository;
import com.joelcode.personalinvestmentportfoliotracker.repositories.TransactionRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.portfolio.allocation.AllocationBreakdownService;
import com.joelcode.personalinvestmentportfoliotracker.services.portfolio.overview.PortfolioOverviewService;
import com.joelcode.personalinvestmentportfoliotracker.services.portfolio.performance.PortfolioPerformanceService;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private PortfolioOverviewService portfolioOverviewService;

    @Mock
    private PortfolioPerformanceService portfolioPerformanceService;

    @Mock
    private AllocationBreakdownService allocationBreakdownService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        // Inject mocks using ReflectionTestUtils
        ReflectionTestUtils.setField(dashboardController, "portfolioOverviewService", portfolioOverviewService);
        ReflectionTestUtils.setField(dashboardController, "portfolioPerformanceService", portfolioPerformanceService);
        ReflectionTestUtils.setField(dashboardController, "allocationBreakdownService", allocationBreakdownService);
        ReflectionTestUtils.setField(dashboardController, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(dashboardController, "accountRepository", accountRepository);
    }

    @Test
    void testGetDashboardForAccount_Success() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        PortfolioOverviewDTO overview = new PortfolioOverviewDTO(UUID.randomUUID(), accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), new ArrayList<>());
        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(UUID.randomUUID(), accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(14), BigDecimal.valueOf(88), BigDecimal.valueOf(9));

        when(portfolioOverviewService.getPortfolioOverviewForAccount(accountId)).thenReturn(overview);
        when(portfolioPerformanceService.getPerformanceForAccount(accountId)).thenReturn(performance);
        when(allocationBreakdownService.getAllocationForAccount(accountId)).thenReturn(new ArrayList<>());
        when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForAccount(accountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(overview, response.getBody().getPortfolioOverview());
        assertEquals(performance, response.getBody().getPortfolioPerformance());
        verify(portfolioOverviewService, times(1)).getPortfolioOverviewForAccount(accountId);
        verify(portfolioPerformanceService, times(1)).getPerformanceForAccount(accountId);
        verify(allocationBreakdownService, times(1)).getAllocationForAccount(accountId);
        verify(transactionRepository, times(1)).findByAccount_AccountId(accountId);
    }

    @Test
    void testGetDashboardForAccount_WithTransactions() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PortfolioOverviewDTO overview = new PortfolioOverviewDTO(userId, accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), new ArrayList<>());
        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(userId, accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(14), BigDecimal.valueOf(88), BigDecimal.valueOf(9));

        // Create mock transactions
        List<Transaction> mockTransactions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(UUID.randomUUID());
            transaction.setCreatedAt(LocalDateTime.now().minusHours(i));
            mockTransactions.add(transaction);
        }

        when(portfolioOverviewService.getPortfolioOverviewForAccount(accountId)).thenReturn(overview);
        when(portfolioPerformanceService.getPerformanceForAccount(accountId)).thenReturn(performance);
        when(allocationBreakdownService.getAllocationForAccount(accountId)).thenReturn(new ArrayList<>());
        when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(mockTransactions);

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForAccount(accountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getRecentTransactions().size());
        verify(transactionRepository, times(1)).findByAccount_AccountId(accountId);
    }

    @Test
    void testGetDashboardForAccount_WithNullOverview() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(UUID.randomUUID(), accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(14), BigDecimal.valueOf(88), BigDecimal.valueOf(9));

        when(portfolioOverviewService.getPortfolioOverviewForAccount(accountId)).thenReturn(null);
        when(portfolioPerformanceService.getPerformanceForAccount(accountId)).thenReturn(performance);
        when(allocationBreakdownService.getAllocationForAccount(accountId)).thenReturn(new ArrayList<>());
        when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForAccount(accountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getPortfolioOverview());
        assertNotNull(response.getBody().getPortfolioPerformance());
    }

    @Test
    void testGetDashboardForUser_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        PortfolioOverviewDTO overview = new PortfolioOverviewDTO(userId, accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), new ArrayList<>());
        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(userId, accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(14), BigDecimal.valueOf(88), BigDecimal.valueOf(9));

        Account mockAccount = new Account();
        mockAccount.setAccountId(accountId);

        when(portfolioOverviewService.getPortfolioOverviewForUser(userId)).thenReturn(overview);
        when(portfolioPerformanceService.getPerformanceForUser(userId)).thenReturn(performance);
        when(allocationBreakdownService.getAllocationForUser(userId)).thenReturn(new ArrayList<>());
        when(accountRepository.findByUser_UserId(userId, Pageable.unpaged())).thenReturn(List.of(mockAccount));
        when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPortfolioOverview());
        assertNotNull(response.getBody().getPortfolioPerformance());
        verify(portfolioOverviewService, times(1)).getPortfolioOverviewForUser(userId);
        verify(portfolioPerformanceService, times(1)).getPerformanceForUser(userId);
        verify(accountRepository, times(1)).findByUser_UserId(userId, Pageable.unpaged());
    }

    @Test
    void testGetDashboardForUser_WithMultipleAccounts() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID account1Id = UUID.randomUUID();
        UUID account2Id = UUID.randomUUID();

        PortfolioOverviewDTO overview = new PortfolioOverviewDTO(userId, account1Id, BigDecimal.valueOf(100000),
                BigDecimal.valueOf(110000), BigDecimal.valueOf(10000), BigDecimal.valueOf(8000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), new ArrayList<>());
        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(userId, account1Id, BigDecimal.valueOf(100000),
                BigDecimal.valueOf(110000), BigDecimal.valueOf(10000), BigDecimal.valueOf(8000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(14), BigDecimal.valueOf(88), BigDecimal.valueOf(9));

        Account account1 = new Account();
        account1.setAccountId(account1Id);
        Account account2 = new Account();
        account2.setAccountId(account2Id);

        when(portfolioOverviewService.getPortfolioOverviewForUser(userId)).thenReturn(overview);
        when(portfolioPerformanceService.getPerformanceForUser(userId)).thenReturn(performance);
        when(allocationBreakdownService.getAllocationForUser(userId)).thenReturn(new ArrayList<>());
        when(accountRepository.findByUser_UserId(userId, Pageable.unpaged())).thenReturn(List.of(account1, account2));
        when(transactionRepository.findByAccount_AccountId(account1Id)).thenReturn(new ArrayList<>());
        when(transactionRepository.findByAccount_AccountId(account2Id)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(accountRepository, times(1)).findByUser_UserId(userId, Pageable.unpaged());
        verify(transactionRepository, times(2)).findByAccount_AccountId(any(UUID.class));
    }

    @Test
    void testGetDashboardForUser_WithBothNull() {
        // Arrange
        UUID userId = UUID.randomUUID();

        when(portfolioOverviewService.getPortfolioOverviewForUser(userId)).thenReturn(null);
        when(portfolioPerformanceService.getPerformanceForUser(userId)).thenReturn(null);
        when(allocationBreakdownService.getAllocationForUser(userId)).thenReturn(new ArrayList<>());
        when(accountRepository.findByUser_UserId(userId, Pageable.unpaged())).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getPortfolioOverview());
        assertNull(response.getBody().getPortfolioPerformance());
    }

    @Test
    void testGetDashboardForUser_LargePortfolio() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        ArrayList<HoldingDTO> holdings = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            HoldingDTO holding = new HoldingDTO();
            holding.setStockSymbol("STOCK" + i);
            holding.setQuantity(BigDecimal.valueOf(100 + i));
            holding.setCurrentPrice(BigDecimal.valueOf(10 + i));
            holdings.add(holding);
        }

        PortfolioOverviewDTO overview = new PortfolioOverviewDTO(userId, accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), holdings);
        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(userId, accountId, BigDecimal.valueOf(50000),
                BigDecimal.valueOf(55000), BigDecimal.valueOf(5000), BigDecimal.valueOf(4000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(14), BigDecimal.valueOf(88), BigDecimal.valueOf(9));

        Account mockAccount = new Account();
        mockAccount.setAccountId(accountId);

        when(portfolioOverviewService.getPortfolioOverviewForUser(userId)).thenReturn(overview);
        when(portfolioPerformanceService.getPerformanceForUser(userId)).thenReturn(performance);
        when(allocationBreakdownService.getAllocationForUser(userId)).thenReturn(new ArrayList<>());
        when(accountRepository.findByUser_UserId(userId, Pageable.unpaged())).thenReturn(List.of(mockAccount));
        when(transactionRepository.findByAccount_AccountId(accountId)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardForUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(55000), response.getBody().getPortfolioOverview().getTotalCostBasis());
        assertEquals(50, response.getBody().getPortfolioOverview().getHoldings().size());
        verify(portfolioOverviewService, times(1)).getPortfolioOverviewForUser(userId);
        verify(portfolioPerformanceService, times(1)).getPerformanceForUser(userId);
    }
}