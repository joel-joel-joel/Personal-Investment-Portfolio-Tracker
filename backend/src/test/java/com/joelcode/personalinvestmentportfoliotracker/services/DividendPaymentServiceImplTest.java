package com.joelcode.personalinvestmentportfoliotracker.services;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.*;
import com.joelcode.personalinvestmentportfoliotracker.repositories.*;
import com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment.DividendPaymentServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment.DividendPaymentValidationService;
import com.joelcode.personalinvestmentportfoliotracker.services.holding.HoldingCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testing dividend payment service layer business logic
public class DividendPaymentServiceImplTest {

    @Mock
    private DividendPaymentRepository paymentRepository;
    @Mock
    private DividendRepository dividendRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private HoldingRepository holdingRepository;
    @Mock
    private DividendPaymentValidationService validationService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private HoldingCalculationService holdingCalculationService;

    @InjectMocks
    private DividendPaymentServiceImpl paymentService;

    private DividendPayment testPayment;
    private Account testAccount;
    private Dividend testDividend;
    private Stock testStock;
    private UUID paymentId;
    private UUID accountId;
    private UUID dividendId;
    private UUID stockId;

    // Set up test data with mock values
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        paymentId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        dividendId = UUID.randomUUID();
        stockId = UUID.randomUUID();

        testAccount = new Account();
        testAccount.setAccountId(accountId);
        testAccount.setAccountBalance(BigDecimal.valueOf(10000));

        testStock = new Stock();
        testStock.setStockId(stockId);
        testStock.setStockCode("AAPL");

        testDividend = new Dividend();
        testDividend.setDividendId(dividendId);
        testDividend.setStock(testStock);
        testDividend.setDividendAmountPerShare(BigDecimal.valueOf(0.25));
        testDividend.setPayDate(LocalDateTime.now());

        testPayment = new DividendPayment();
        testPayment.setPaymentId(paymentId);
        testPayment.setAccount(testAccount);
        testPayment.setStock(testStock);
        testPayment.setDividend(testDividend);
        testPayment.setShareQuantity(BigDecimal.valueOf(100));
        testPayment.setTotalDividendAmount(BigDecimal.valueOf(25));
    }

    // Test payment creation
    @Test
    void testCreateDividendPayment_Success() {
        DividendPaymentCreateRequest request = new DividendPaymentCreateRequest(
                accountId,
                dividendId,
                BigDecimal.valueOf(100),
                LocalDateTime.now()
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        when(dividendRepository.findById(dividendId)).thenReturn(Optional.of(testDividend));
        when(paymentRepository.existsByAccountAndDividend(any(), any())).thenReturn(false);
        when(paymentRepository.save(any(DividendPayment.class))).thenReturn(testPayment);
        when(holdingCalculationService.calculateTotalUnrealizedGain(accountId)).thenReturn(BigDecimal.ZERO);

        DividendPaymentDTO result = paymentService.createDividendPayment(request);

        assertNotNull(result);
        verify(paymentRepository, times(1)).save(any(DividendPayment.class));
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), (Object) any());
    }

    // Test retrieving payment by id
    @Test
    void testGetDividendPaymentById_ReturnsCorrectDTO() {
        when(validationService.validatePaymentExists(paymentId)).thenReturn(testPayment);

        DividendPaymentDTO result = paymentService.getDividendPaymentById(paymentId);

        assertNotNull(result);
        assertEquals(paymentId, result.getPaymentId());
    }

    // Test retrieving payments by account
    @Test
    void testGetDividendPaymentsByAccount_ReturnsCorrectList() {
        when(paymentRepository.findPaymentsByAccountOrderByDate(accountId))
                .thenReturn(List.of(testPayment));

        List<DividendPaymentDTO> result = paymentService.getDividendPaymentsByAccount(accountId);

        assertEquals(1, result.size());
    }

    // Test deleting payment
    @Test
    void testDeleteDividendPayment_Success() {
        when(validationService.validatePaymentExists(paymentId)).thenReturn(testPayment);

        paymentService.deleteDividendPayment(paymentId);

        verify(paymentRepository, times(1)).delete(testPayment);
    }
}
