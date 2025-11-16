package com.joelcode.personalinvestmentportfoliotracker.services.dividend;

import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.dividend.DividendDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DividendService {

    DividendDTO createDividend(DividendCreateRequest request);

    DividendDTO getDividendById(UUID id);

    List<DividendDTO> getAllDividends();

    void deleteDividend(UUID id);
}
