package com.joelcode.personalinvestmentportfoliotracker.mapping;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividendpayment.DividendPaymentDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.Dividend;
import com.joelcode.personalinvestmentportfoliotracker.entities.DividendPayment;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.DividendPaymentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DividendPaymentMapperTest {

    @Test
    void testToEntity_Success() {
        // Arrange
        Account account = new Account();
        account.setAccountId(UUID.randomUUID());

        Dividend dividend = new Dividend();
        dividend.setAmountPerShare(BigDecimal.valueOf(2.5));

        Stock stock = new Stock();
        stock.setStockId(UUID.randomUUID());

        LocalDateTime now = LocalDateTime.now();

        DividendPaymentCreateRequest request = new DividendPaymentCreateRequest(
                account.getAccountId(),
                stock.getStockId(),
                BigDecimal.valueOf(100),
                now
        );

        // Act
        DividendPayment payment = DividendPaymentMapper.toEntity(request, account, dividend, stock);

        // Assert
        assertNotNull(payment);
        assertEquals(account, payment.getAccount());
        assertEquals(stock, payment.getStock());
        assertEquals(dividend, payment.getDividend());
        assertEquals(BigDecimal.valueOf(100), payment.getShareQuantity());
        assertEquals(now, payment.getPaymentDate());
        assertEquals(DividendPayment.PaymentStatus.PAID, payment.getStatus());
    }

    @Test
    void testToEntity_NullRequest() {
        // Arrange
        Account account = new Account();
        Dividend dividend = new Dividend();
        Stock stock = new Stock();

        // Act
        DividendPayment payment = DividendPaymentMapper.toEntity(null, account, dividend, stock);

        // Assert
        assertNull(payment);
    }

    @Test
    void testToEntity_NullAccount() {
        // Arrange
        Dividend dividend = new Dividend();
        Stock stock = new Stock();
        DividendPaymentCreateRequest request = new DividendPaymentCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                LocalDateTime.now()
        );

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            DividendPaymentMapper.toEntity(request, null, dividend, stock);
        });
    }

    @Test
    void testToEntity_NullDividend() {
        // Arrange
        Account account = new Account();
        Stock stock = new Stock();
        DividendPaymentCreateRequest request = new DividendPaymentCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                LocalDateTime.now()
        );

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            DividendPaymentMapper.toEntity(request, account, null, stock);
        });
    }

    @Test
    void testToEntity_NullStock() {
        // Arrange
        Account account = new Account();
        Dividend dividend = new Dividend();
        DividendPaymentCreateRequest request = new DividendPaymentCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                LocalDateTime.now()
        );

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            DividendPaymentMapper.toEntity(request, account, dividend, null);
        });
    }

    @Test
    void testToDTO_Success() {
        // Arrange
        DividendPayment payment = new DividendPayment();
        UUID paymentId = UUID.randomUUID();
        payment.setPaymentId(paymentId);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID());
        payment.setAccount(account);

        Stock stock = new Stock();
        stock.setStockId(UUID.randomUUID());
        payment.setStock(stock);

        payment.setShareQuantity(BigDecimal.valueOf(100));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTotalDividendAmount(BigDecimal.valueOf(250.0));

        // Act
        DividendPaymentDTO dto = DividendPaymentMapper.toDTO(payment);

        // Assert
        assertNotNull(dto);
        assertEquals(paymentId, dto.getPaymentId());
        assertEquals(account.getAccountId(), dto.getAccountId());
        assertEquals(stock.getStockId(), dto.getStockId());
    }

    @Test
    void testToDTO_Null() {
        // Act
        DividendPaymentDTO dto = DividendPaymentMapper.toDTO(null);

        // Assert
        assertNull(dto);
    }
}