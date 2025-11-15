package com.joelcode.personalinvestmentportfoliotracker.services.stock;

import com.joelcode.personalinvestmentportfoliotracker.dto.stock.StockCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.stock.StockDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.stock.StockUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface StockService {

    StockDTO createStock(StockCreateRequest request);

    StockDTO updateStock(UUID id, StockUpdateRequest request);

    StockDTO getStockById(UUID id);

    List<StockDTO> getAllStocks();

    void deleteStock(UUID id);
}
