package com.joelcode.personalinvestmentportfoliotracker.services.scheduler;

import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.logging.BetterStackLogger;
import com.joelcode.personalinvestmentportfoliotracker.repositories.AccountRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.portfoliosnapshot.PortfolioSnapshotCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Profile("!test")
public class PortfolioSnapshotScheduler {

    private final AccountRepository accountRepository;
    private final PortfolioSnapshotCalculationService snapshotCalculationService;
    private final BetterStackLogger logger;

    @Autowired
    public PortfolioSnapshotScheduler(AccountRepository accountRepository,
                                      PortfolioSnapshotCalculationService snapshotCalculationService,
                                      @Autowired(required = false) BetterStackLogger logger) {
        this.accountRepository = accountRepository;
        this.snapshotCalculationService = snapshotCalculationService;
        this.logger = logger;
    }

    /**
     * Scheduled task to create daily portfolio snapshots for all accounts
     * Runs every day at midnight (00:00:00)
     * Cron format: second, minute, hour, day of month, month, day of week
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void createDailySnapshotsForAllAccounts() {
        logInfo("Starting daily portfolio snapshot generation for all accounts");

        List<Account> accounts = accountRepository.findAll();
        int successCount = 0;
        int failureCount = 0;

        for (Account account : accounts) {
            try {
                UUID accountId = account.getAccountId();
                snapshotCalculationService.generateSnapshotForToday(accountId);
                successCount++;
                logInfo("Generated snapshot for account: " + account.getAccountName() + " (ID: " + accountId + ")");
            } catch (RuntimeException e) {
                failureCount++;
                // Don't stop the entire process if one account fails
                if (e.getMessage().contains("already exists")) {
                    logInfo("Snapshot already exists for account: " + account.getAccountName() + " - skipping");
                } else {
                    logError("Failed to generate snapshot for account: " + account.getAccountName() + " - " + e.getMessage());
                }
            } catch (Exception e) {
                failureCount++;
                logError("Unexpected error generating snapshot for account: " + account.getAccountName() + " - " + e.getMessage());
            }
        }

        logInfo("Daily snapshot generation completed. Success: " + successCount + ", Failed: " + failureCount + ", Total: " + accounts.size());
    }

    /**
     * Manual trigger to generate snapshot for a specific account
     * Can be called from a controller endpoint
     */
    public void createSnapshotForAccount(UUID accountId) {
        try {
            snapshotCalculationService.generateSnapshotForToday(accountId);
            logInfo("Manually generated snapshot for account ID: " + accountId);
        } catch (Exception e) {
            logError("Failed to manually generate snapshot for account ID: " + accountId + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Manual trigger to generate snapshots for all accounts
     * Can be called from a controller endpoint
     */
    public int createSnapshotsForAllAccounts() {
        logInfo("Manually triggering snapshot generation for all accounts");

        List<Account> accounts = accountRepository.findAll();
        int successCount = 0;

        for (Account account : accounts) {
            try {
                UUID accountId = account.getAccountId();
                snapshotCalculationService.generateSnapshotForToday(accountId);
                successCount++;
            } catch (RuntimeException e) {
                if (!e.getMessage().contains("already exists")) {
                    logError("Failed to generate snapshot for account: " + account.getAccountName() + " - " + e.getMessage());
                }
            } catch (Exception e) {
                logError("Unexpected error for account: " + account.getAccountName() + " - " + e.getMessage());
            }
        }

        logInfo("Manual snapshot generation completed. Generated: " + successCount + " snapshots");
        return successCount;
    }

    // Helper methods for logging
    private void logInfo(String message) {
        if (logger != null) {
            logger.info(message);
        } else {
            System.out.println("[INFO] " + message);
        }
    }

    private void logError(String message) {
        if (logger != null) {
            logger.error(message);
        } else {
            System.err.println("[ERROR] " + message);
        }
    }
}
