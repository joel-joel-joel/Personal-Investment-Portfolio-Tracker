package com.joelcode.personalinvestmentportfoliotracker.controllers.portfoliocontrollers;

import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.AllocationBreakdownDTO;
import com.joelcode.personalinvestmentportfoliotracker.services.portfolio.allocation.AllocationBreakdownService;
import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/allocation")
@Profile("!test")
public class AllocationBreakdownController {

    @Autowired
    AllocationBreakdownService allocationService;

    // Get allocation for a specific account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<AllocationBreakdownDTO>> getAllocationForAccount(@PathVariable UUID accountId) {
        List<AllocationBreakdownDTO> allocations = allocationService.getAllocationForAccount(accountId);
        return ResponseEntity.ok(allocations);
    }

    // Get allocation for all accounts of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AllocationBreakdownDTO>> getAllocationForUser(@PathVariable UUID userId) {
        List<AllocationBreakdownDTO> allocations = allocationService.getAllocationForUser(userId);
        return ResponseEntity.ok(allocations);
    }
}
