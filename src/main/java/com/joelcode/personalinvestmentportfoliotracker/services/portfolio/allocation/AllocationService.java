package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.allocation;

import com.joelcode.personalinvestmentportfoliotracker.dto.model.AllocationBreakdownDTO;

import java.util.List;
import java.util.UUID;

public interface AllocationService {
    List<AllocationBreakdownDTO> getAllocationForAccount(UUID accountId);
}

