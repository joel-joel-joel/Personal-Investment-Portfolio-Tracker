package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.allocation;

import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.AllocationBreakdownDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Holding;
import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import com.joelcode.personalinvestmentportfoliotracker.repositories.HoldingRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.holding.HoldingCalculationService;
import com.joelcode.personalinvestmentportfoliotracker.services.holding.HoldingService;
import com.joelcode.personalinvestmentportfoliotracker.services.user.UserValidationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AllocationBreakdownServiceImpl implements AllocationBreakdownService {

    // Define key fields
    private final HoldingService holdingService;
    private final HoldingCalculationService holdingCalcService;
    private final HoldingRepository holdingRepository;
    private final UserValidationService userValidationService;

    // Constructor
    public AllocationBreakdownServiceImpl(HoldingService holdingService,
                                          HoldingCalculationService holdingCalcService,
                                          HoldingRepository holdingRepository,
                                          UserValidationService userValidationService) {
        this.holdingService = holdingService;
        this.holdingCalcService = holdingCalcService;
        this.holdingRepository = holdingRepository;
        this.userValidationService = userValidationService;
    }

    // Interface methods
    @Override
    public List<AllocationBreakdownDTO> getAllocationForAccount(UUID accountId) {

        List<Holding> holdings = holdingRepository.getHoldingsEntitiesByAccount(accountId);

        BigDecimal totalValue = holdings.stream()
                .map(holdingCalcService::calculateCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return holdings.stream()
                .map(h -> {
                    BigDecimal value = holdingCalcService.calculateCurrentValue(h);
                    BigDecimal percentage =
                            totalValue.compareTo(BigDecimal.ZERO) == 0
                                    ? BigDecimal.ZERO
                                    : value.multiply(BigDecimal.valueOf(100))
                                    .divide(totalValue, 4, RoundingMode.HALF_UP);

                    AllocationBreakdownDTO dto = new AllocationBreakdownDTO();
                    dto.setStockCode(h.getStock().getStockCode());
                    dto.setPercentage(percentage);
                    dto.setCurrentValue(value);
                    return dto;
                })
                .toList();
    }

    public List<AllocationBreakdownDTO> getAllocationForUser(UUID userId) {
        // Validate user exists
        User user = userValidationService.validateUserExists(userId);

        // Aggregate allocations across all accounts
        return user.getAccounts().stream()
                .flatMap(account -> getAllocationForAccount(account.getAccountId()).stream())
                .collect(Collectors.toList());
    }

}

