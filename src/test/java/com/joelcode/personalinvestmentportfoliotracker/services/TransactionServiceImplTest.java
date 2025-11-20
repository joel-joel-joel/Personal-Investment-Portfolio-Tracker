package com.joelcode.personalinvestmentportfoliotracker.services;

import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Transaction;
import com.joelcode.personalinvestmentportfoliotracker.repositories.TransactionRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.TransactionMapper;
import com.joelcode.personalinvestmentportfoliotracker.services.transaction.TransactionServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.transaction.TransactionValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testing Transaction service layer business logic
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionValidationService transactionValidationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction testTransaction;
    private UUID transactionId;
    private UUID accountId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        transactionId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        testTransaction = new Transaction();
        testTransaction.setTransactionId(transactionId);
        testTransaction.setAccountId(accountId);
        testTransaction.setPricePerShare(BigDecimal.valueOf(50));
        testTransaction.setShareQuantity(BigDecimal.valueOf(10));
    }

    // Test creating a transaction
    @Test
    void testCreateTransaction_Success() {
        TransactionCreateRequest request = new TransactionCreateRequest();
        request.setTransactionType(Transaction.TransactionType.BUY);
        request.setPricePerShare(BigDecimal.valueOf(50));
        request.setShareQuantity(BigDecimal.valueOf(10));
        request.setAccountId(accountId);

        // Validation
        doNothing().when(transactionValidationService).validateTransactionType(any());
        doNothing().when(transactionValidationService).validateSufficientBalance(accountId, 500.0);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        try (MockedStatic<TransactionMapper> mapperMock = Mockito.mockStatic(TransactionMapper.class)) {
            mapperMock.when(() -> TransactionMapper.toEntity(request)).thenReturn(testTransaction);
            mapperMock.when(() -> TransactionMapper.toDTO(testTransaction)).thenReturn(new TransactionDTO());

            TransactionDTO result = transactionService.createTransaction(request);

            assertNotNull(result);
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }
    }

    // Test get transaction by ID
    @Test
    void testGetTransactionById_ReturnsCorrectDTO() {
        when(transactionValidationService.validateTransactionExists(transactionId)).thenReturn(testTransaction);

        try (MockedStatic<TransactionMapper> mapperMock = Mockito.mockStatic(TransactionMapper.class)) {
            mapperMock.when(() -> TransactionMapper.toDTO(testTransaction)).thenReturn(new TransactionDTO());

            TransactionDTO result = transactionService.getTransactionById(transactionId);

            assertNotNull(result);
        }
    }

    // Test get all transactions
    @Test
    void testGetAllTransactions_ReturnsCorrectList() {
        when(transactionRepository.findAll()).thenReturn(List.of(testTransaction));

        try (MockedStatic<TransactionMapper> mapperMock = Mockito.mockStatic(TransactionMapper.class)) {
            mapperMock.when(() -> TransactionMapper.toDTO(testTransaction)).thenReturn(new TransactionDTO());

            List<TransactionDTO> result = transactionService.getAllTransactions();

            assertEquals(1, result.size());
        }
    }

    // Test delete transaction
    @Test
    void testDeleteTransaction_Success() {
        when(transactionValidationService.validateTransactionExists(transactionId)).thenReturn(testTransaction);

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository, times(1)).delete(testTransaction);
    }
}
