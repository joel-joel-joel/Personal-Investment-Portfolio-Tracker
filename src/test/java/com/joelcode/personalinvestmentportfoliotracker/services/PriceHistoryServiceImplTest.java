package com.joelcode.personalinvestmentportfoliotracker.services;

import com.joelcode.personalinvestmentportfoliotracker.controllers.WebSocketController;
import com.joelcode.personalinvestmentportfoliotracker.dto.pricehistory.PriceHistoryCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.pricehistory.PriceHistoryDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.PriceHistory;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import com.joelcode.personalinvestmentportfoliotracker.repositories.PriceHistoryRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.PriceHistoryMapper;
import com.joelcode.personalinvestmentportfoliotracker.services.pricehistory.PriceHistoryServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.pricehistory.PriceHistoryValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testing PriceHistory service layer business logic
public class PriceHistoryServiceImplTest {

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private PriceHistoryValidationService validationService;

    @Mock
    private WebSocketController webSocketController;

    @InjectMocks
    private PriceHistoryServiceImpl priceHistoryService;

    private PriceHistory testPriceHistory;
    private Stock testStock;
    private UUID priceHistoryId;
    private UUID stockId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        priceHistoryId = UUID.randomUUID();
        stockId = UUID.randomUUID();

        // Initialize Stock to prevent NPE
        testStock = new Stock();
        testStock.setStockId(stockId);
        testStock.setStockCode("TEST");

        // Initialize PriceHistory with Stock
        testPriceHistory = new PriceHistory();
        testPriceHistory.setPriceHistoryId(priceHistoryId);
        testPriceHistory.setStock(testStock);
        testPriceHistory.setClosePrice(BigDecimal.valueOf(100));
    }

    @Test
    void testCreatePriceHistory_Success() {
        PriceHistoryCreateRequest request = new PriceHistoryCreateRequest();
        request.setStockId(stockId);
        request.setClosePrice(BigDecimal.valueOf(100));

        doNothing().when(validationService).validateCreateRequest(any(), any());
        when(priceHistoryRepository.save(any(PriceHistory.class))).thenReturn(testPriceHistory);

        try (MockedStatic<PriceHistoryMapper> mapperMock = Mockito.mockStatic(PriceHistoryMapper.class)) {
            mapperMock.when(() -> PriceHistoryMapper.toEntity(request)).thenReturn(testPriceHistory);
            mapperMock.when(() -> PriceHistoryMapper.toDTO(testPriceHistory)).thenReturn(new PriceHistoryDTO());

            PriceHistoryDTO result = priceHistoryService.createPriceHistory(request);

            assertNotNull(result);
            verify(priceHistoryRepository, times(1)).save(any(PriceHistory.class));
        }
    }

    @Test
    void testGetPriceHistoryById_ReturnsCorrectDTO() {
        when(validationService.validatePriceHistoryExists(priceHistoryId)).thenReturn(testPriceHistory);

        try (MockedStatic<PriceHistoryMapper> mapperMock = Mockito.mockStatic(PriceHistoryMapper.class)) {
            mapperMock.when(() -> PriceHistoryMapper.toDTO(testPriceHistory)).thenReturn(new PriceHistoryDTO());

            PriceHistoryDTO result = priceHistoryService.getPriceHistoryById(priceHistoryId);

            assertNotNull(result);
        }
    }

    @Test
    void testGetAllPriceHistories_ReturnsCorrectList() {
        when(priceHistoryRepository.findAll()).thenReturn(List.of(testPriceHistory));

        try (MockedStatic<PriceHistoryMapper> mapperMock = Mockito.mockStatic(PriceHistoryMapper.class)) {
            mapperMock.when(() -> PriceHistoryMapper.toDTO(testPriceHistory)).thenReturn(new PriceHistoryDTO());

            List<PriceHistoryDTO> result = priceHistoryService.getAllPriceHistories();

            assertEquals(1, result.size());
        }
    }

    @Test
    void testDeletePriceHistory_Success() {
        when(validationService.validatePriceHistoryExists(priceHistoryId)).thenReturn(testPriceHistory);

        priceHistoryService.deletePriceHistory(priceHistoryId);

        verify(priceHistoryRepository, times(1)).delete(testPriceHistory);
    }

    @Test
    void testGetCurrentPrice_ReturnsPrice() {
        when(priceHistoryRepository.findLatestPriceByStockId(stockId)).thenReturn(Optional.of(BigDecimal.valueOf(100)));

        BigDecimal price = priceHistoryService.getCurrentPrice(stockId);

        assertEquals(BigDecimal.valueOf(100), price);
    }

    @Test
    void testGetPriceHistoryForStock_ReturnsList() {
        when(priceHistoryRepository.findByStock_IdOrderByDateAsc(stockId)).thenReturn(List.of(testPriceHistory));

        try (MockedStatic<PriceHistoryMapper> mapperMock = Mockito.mockStatic(PriceHistoryMapper.class)) {
            mapperMock.when(() -> PriceHistoryMapper.toDTO(testPriceHistory)).thenReturn(new PriceHistoryDTO());

            List<PriceHistoryDTO> result = priceHistoryService.getPriceHistoryForStock(stockId);

            assertEquals(1, result.size());
        }
    }

    @Test
    void testGetLatestPriceForStock_ReturnsDTO() {
        when(priceHistoryRepository.findTopByStock_IdOrderByDateDesc(stockId)).thenReturn(Optional.of(testPriceHistory));

        try (MockedStatic<PriceHistoryMapper> mapperMock = Mockito.mockStatic(PriceHistoryMapper.class)) {
            mapperMock.when(() -> PriceHistoryMapper.toDTO(testPriceHistory)).thenReturn(new PriceHistoryDTO());

            priceHistoryService.getLatestPriceForStock(stockId);

            verify(webSocketController, times(1)).broadcastStockPriceUpdate(eq(stockId), eq("TEST"), any());
        }
    }
}