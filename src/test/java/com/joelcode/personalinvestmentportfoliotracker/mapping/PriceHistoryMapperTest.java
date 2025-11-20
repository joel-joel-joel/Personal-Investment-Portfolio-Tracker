package com.joelcode.personalinvestmentportfoliotracker.mapping;

import com.joelcode.personalinvestmentportfoliotracker.dto.pricehistory.PriceHistoryCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.pricehistory.PriceHistoryDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.PriceHistory;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.PriceHistoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PriceHistoryMapperTest {

    @Test
    void testToEntity_Success() {
        // Arrange
        UUID stockId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        PriceHistoryCreateRequest request = new PriceHistoryCreateRequest(
                now,
                stockId,
                BigDecimal.valueOf(150.0)
        );

        // Act
        PriceHistory priceHistory = PriceHistoryMapper.toEntity(request);

        // Assert
        assertNotNull(priceHistory);
        assertNotNull(priceHistory.getStock());
        assertEquals(stockId, priceHistory.getStock().getStockId());
        assertEquals(BigDecimal.valueOf(150.0), priceHistory.getClosePrice());
        assertEquals(now, priceHistory.getCloseDate());
    }

    @Test
    void testToEntity_NullRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            PriceHistoryMapper.toEntity(null);
        });
    }

    @Test
    void testToDTO_Success() {
        // Arrange
        PriceHistory priceHistory = new PriceHistory();
        UUID priceHistoryId = UUID.randomUUID();
        priceHistory.setPriceHistoryId(priceHistoryId);

        Stock stock = new Stock();
        UUID stockId = UUID.randomUUID();
        stock.setStockId(stockId);
        priceHistory.setStock(stock);

        LocalDateTime now = LocalDateTime.now();

        priceHistory.setClosePrice(BigDecimal.valueOf(155.0));
        priceHistory.setCloseDate(now);

        // Act
        PriceHistoryDTO dto = PriceHistoryMapper.toDTO(priceHistory);

        // Assert
        assertNotNull(dto);
        assertEquals(priceHistoryId, dto.getPriceHistoryId());
        assertEquals(stockId, dto.getStockId());
        assertEquals(BigDecimal.valueOf(155.0), dto.getClosePrice());
        assertEquals(now, dto.getCloseDate());
    }

    @Test
    void testToDTO_Null() {
        // Act
        PriceHistoryDTO dto = PriceHistoryMapper.toDTO(null);

        // Assert
        assertNull(dto);
    }
}