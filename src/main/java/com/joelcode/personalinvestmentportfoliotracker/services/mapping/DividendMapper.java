package com.joelcode.personalinvestmentportfoliotracker.services.mapping;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Dividend;
import com.joelcode.personalinvestmentportfoliotracker.entities.Stock;
import com.joelcode.personalinvestmentportfoliotracker.repositories.StockRepository;

public class DividendMapper {

    private static StockRepository stockRepository;

    // Convert dividend creation request DTO to entity
    public static Dividend toEntity(DividendCreateRequest request) {
        Dividend dividend = new Dividend();
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
        dividend.setStockId(request.getStockId());
        dividend.setAmountPerShare(request.getAmountPerShare());
        dividend.setPayDate(request.getPayDate());
        return dividend;
    }

    // Convert dividend entity to dividend response DTO
    public static DividendDTO toDTO(Dividend dividend) {
        if (dividend == null) return null;
        return new DividendDTO(
                dividend.getDividendId(),
                dividend.getStock().getStockId(),
                dividend.getAmountPerShare(),
                dividend.getPayDate());
    }
}
