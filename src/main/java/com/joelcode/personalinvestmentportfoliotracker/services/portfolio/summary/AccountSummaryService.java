package com.joelcode.personalinvestmentportfoliotracker.services.portfolio.summary;

import com.joelcode.personalinvestmentportfoliotracker.dto.portfolio.AccountSummaryDTO;

import java.util.UUID;

public interface AccountSummaryService {

    AccountSummaryDTO getAccountSummary(UUID accountId);

}
