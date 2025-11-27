package com.joelcode.personalinvestmentportfoliotracker.services;


import com.joelcode.personalinvestmentportfoliotracker.dto.account.AccountCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.account.AccountDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.account.AccountUpdateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.holding.HoldingDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.*;
import com.joelcode.personalinvestmentportfoliotracker.repositories.AccountRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.account.AccountServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.account.AccountValidationService;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.HoldingMapper;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.TransactionMapper;
import com.joelcode.personalinvestmentportfoliotracker.services.pricehistory.PriceHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testing account service layer business logic
public class AccountServiceImplTest {

    // Define mock key fields
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountValidationService accountValidationService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private HoldingMapper holdingMapper;

    @Mock
    private PriceHistoryService priceHistoryService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private UUID accountId;

    // Set up a test account with mock values
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        accountId = UUID.randomUUID();
        testAccount = new Account();
        testAccount.setUser(new User());
        testAccount.setUserId(UUID.randomUUID());
        testAccount.setAccountId(accountId);
        testAccount.setAccountName("Test Account");
        testAccount.setAccountBalance(BigDecimal.valueOf(1000));
        testAccount.setTransactions(new ArrayList<>());
        testAccount.setHoldings(new ArrayList<>());
    }

    // Test account creation
    @Test
    void testCreateAccount_Success() {
        // Create an account creation request
        AccountCreateRequest request = new AccountCreateRequest();
        request.setAccountName("Test Account");

        // Make a test account
        Account savedAccount = new Account();
        savedAccount.setAccountId(accountId);
        savedAccount.setAccountName("Test Account");
        savedAccount.setAccountBalance(BigDecimal.ZERO);


        // Any account save to the repository returns the saved account
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Create the response from the request
        AccountDTO result = accountService.createAccount(request);

        // Ensure that the dto is not empty
        assertNotNull(result);
        // Ensure that the name matches the saved account
        assertEquals("Test Account", result.getAccountName());
        // Ensure that the repository did not save more than once
        verify(accountRepository, times(1)).save(any(Account.class));
        // Verify websocket endpoint is correct
        verify(messagingTemplate, times(1))
                .convertAndSend(startsWith("/topic/portfolio/"), (Object) any());
    }

    // Test retrieving an account by id
    @Test
    void testGetAccountById_ReturnsCorrectDTO() {

        // Any account saved returns the test account
        when(accountValidationService.validateAccountExistsById(accountId))
                .thenReturn(testAccount);
        // Attempt to retrieve account and store as a response dto
        AccountDTO result = accountService.getAccountById(accountId);

        // Ensure the dto is not empty
        assertNotNull(result);
        // Ensure that the accountId matches
        assertEquals(accountId, result.getAccountId());
    }

    // Test retrieving all accounts
    @Test
    void testGetAllAccounts_ReturnsCorrectList() {

        // Any account find all is called return a list of test accounts
        when (accountRepository.findAll()).thenReturn(List.of(testAccount));

        // Store the list of test accounts in the list of dtos
        List<AccountDTO> result = accountService.getAllAccounts();

        // Verify that only one object is in the list
        assertEquals(1, result.size());
        // Verify accountId matches
        assertEquals(accountId, result.get(0).getAccountId());
    }

    // Test update account
    @Test
    void testUpdateAccount_Success() {
        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setAccountName("Updated Account");

        // Ensures that both the validation check and repo save return the test account
        when(accountValidationService.validateAccountExistsById(accountId))
                .thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Create respnose dto
        AccountDTO result = accountService.updateAccount(accountId, request);

        // Run check to make sure the account was updated properly
        assertEquals("Updated Account", result.getAccountName());
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(messagingTemplate, times(1))
                .convertAndSend(startsWith("/topic/portfolio/"), (Object) any());
    }

    // Test deleting an account
    @Test
    void testDeleteAccount_Success() {
        when(accountValidationService.validateAccountExistsById(accountId))
                .thenReturn(testAccount);

        accountService.deleteAccount(accountId);

        verify(accountRepository, times(1)).delete(testAccount);
    }

    // Test update an account balance
    @Test
    void testUpdateAccountBalance_Success() {
        BigDecimal amount = BigDecimal.valueOf(200);

        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Update the account balance
        accountService.updateAccountBalance(testAccount, amount);

        // Test if balance was updated
        assertEquals(BigDecimal.valueOf(1200), testAccount.getAccountBalance());
        verify(messagingTemplate, times(1))
                .convertAndSend(startsWith("/topic/portfolio/"), (Object) any());
    }

    // Test retrieving transaction for an account
    @Test
    void testGetTransactionsForAccount_ReturnsCorrectList() {
        Transaction t = new Transaction();
        testAccount.getTransactions().add(t);

        TransactionDTO dto = new TransactionDTO();

        // Ensure dto is returned upon validation and mapping to dto
        when(accountValidationService.validateAccountExistsById(accountId))
                .thenReturn(testAccount);
        try (MockedStatic<TransactionMapper> mockedTransactionMapper = Mockito.mockStatic(TransactionMapper.class)) {
            mockedTransactionMapper.when(() -> TransactionMapper.toDTO(t)).thenReturn(dto);

            List<TransactionDTO> result = accountService.getTransactionsForAccount(accountId);

            assertEquals(1, result.size());
        }

    }

    // Test retrieving holdings for an account
    @Test
    void testGetHoldingsForAccount_ReturnsCorrectList() {
        Holding h = new Holding();
        Stock stock = new Stock();
        stock.setStockId(UUID.randomUUID());
        h.setStock(stock);

        testAccount.getHoldings().add(h);

        when(accountValidationService.validateAccountExistsById(accountId))
                .thenReturn(testAccount);
        UUID stockId = h.getStock().getStockId();
        when(priceHistoryService.getCurrentPrice(stockId)).thenReturn(BigDecimal.TEN);
        try (MockedStatic<HoldingMapper> mockedHoldingMapper = Mockito.mockStatic(HoldingMapper.class)) {
            mockedHoldingMapper.when(() -> HoldingMapper.toDTO(h, BigDecimal.TEN))
                    .thenReturn(new HoldingDTO());

            List<HoldingDTO> result = accountService.getHoldingsForAccount(accountId);

            assertEquals(1, result.size());
        }
    }

}
