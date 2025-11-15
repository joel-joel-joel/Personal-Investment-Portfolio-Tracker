package com.joelcode.personalinvestmentportfoliotracker.services.dividend;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.entities.Dividend;
import com.joelcode.personalinvestmentportfoliotracker.repositories.DividendRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.DividendMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DividendServiceImpl implements DividendService {

    // Define key fields
    private final DividendRepository dividendRepository;
    private final DividendValidationService dividendValidationService;

    // Constructor
    public DividendServiceImpl(DividendRepository dividendRepository,
                               DividendValidationService dividendValidationService) {
        this.dividendRepository = dividendRepository;
        this.dividendValidationService = dividendValidationService;
    }

    // Create dividend entity from request dto
    @Override
    public DividendDTO createDividend(DividendCreateRequest request) {

        // Validate fields
        dividendValidationService.validateCreateRequest(
                request.getAmountPerShare(),
                request.getPayDate()
        );

        // Map request -> entity
        Dividend dividend = DividendMapper.toEntity(request);

        // Save to DB
        dividend = dividendRepository.save(dividend);

        // Convert entity -> DTO
        return DividendMapper.toDTO(dividend);
    }

    @Override
    public DividendDTO getDividendById(UUID dividendId) {
        Dividend dividend = dividendValidationService.validateDividendExists(dividendId);
        return DividendMapper.toDTO(dividend);
    }

    @Override
    public List<DividendDTO> getAllDividends() {
        return dividendRepository.findAll()
                .stream()
                .map(DividendMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDividend(UUID dividendId) {
        Dividend dividend = dividendValidationService.validateDividendExists(dividendId);
        dividendRepository.delete(dividend);
    }
}
