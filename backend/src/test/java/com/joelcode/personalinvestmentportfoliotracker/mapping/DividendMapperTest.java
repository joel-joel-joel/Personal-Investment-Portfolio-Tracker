package com.joelcode.personalinvestmentportfoliotracker.mapping;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Dividend;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.DividendMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DividendMapperTest {

    @Test
    void testToEntity_Success() {
        // Arrange
        Stock stock = new Stock();
        stock.setStockId(UUID.randomUUID());

        LocalDateTime now = LocalDateTime.now();

        DividendCreateRequest request = new DividendCreateRequest(stock.getStockId(), BigDecimal.valueOf(2.5), now);

        // Act
        Dividend dividend = DividendMapper.toEntity(request, stock);

        // Assert
        assertNotNull(dividend);
        assertEquals(BigDecimal.valueOf(2.5), dividend.getDividendAmountPerShare());
        assertEquals(now, dividend.getPayDate());
        assertEquals(stock, dividend.getStock());
    }

    @Test
    void testToEntity_NullRequest() {
        // Arrange
        Stock stock = new Stock();

        // Act
        Dividend dividend = DividendMapper.toEntity(null, stock);

        // Assert
        assertNull(dividend);
    }

    @Test
    void testToEntity_NullStock() {
        // Arrange
        DividendCreateRequest request = new DividendCreateRequest(UUID.randomUUID(), BigDecimal.valueOf(2.5), LocalDateTime.now());

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            DividendMapper.toEntity(request, null);
        });
    }

    @Test
    void testToDTO_Success() {
        // Arrange
        Dividend dividend = new Dividend();
        UUID dividendId = UUID.randomUUID();
        dividend.setDividendId(dividendId);

        Stock stock = new Stock();
        stock.setStockId(UUID.randomUUID());
        dividend.setStock(stock);

        dividend.setDividendAmountPerShare(BigDecimal.valueOf(3.0));
        dividend.setPayDate(LocalDateTime.now());

        // Act
        DividendDTO dto = DividendMapper.toDTO(dividend);

        // Assert
        assertNotNull(dto);
        assertEquals(dividendId, dto.getDividendId());
        assertEquals(stock.getStockId(), dto.getStockId());
        assertEquals(BigDecimal.valueOf(3.0), dto.getDividendPerShare());
    }

    @Test
    void testToDTO_Null() {
        // Act
        DividendDTO dto = DividendMapper.toDTO(null);

        // Assert
        assertNull(dto);
    }
}