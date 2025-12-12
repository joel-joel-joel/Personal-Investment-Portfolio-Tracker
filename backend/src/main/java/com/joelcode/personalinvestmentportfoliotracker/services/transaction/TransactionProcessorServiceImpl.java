package com.joelcode.personalinvestmentportfoliotracker.services.transaction;

import com.joelcode.personalinvestmentportfoliotracker.controllers.WebSocketController;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.Holding;
import com.joelcode.personalinvestmentportfoliotracker.repositories.AccountRepository;
import com.joelcode.personalinvestmentportfoliotracker.repositories.HoldingRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.account.AccountService;
import com.joelcode.personalinvestmentportfoliotracker.services.dividendpayment.DividendPaymentCalculationService;
import com.joelcode.personalinvestmentportfoliotracker.services.holding.HoldingCalculationService;
import com.joelcode.personalinvestmentportfoliotracker.services.holding.HoldingService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Profile("!test")
public class TransactionProcessorServiceImpl implements TransactionProcessorService {

    // Define key fields
    private final TransactionService transactionService;
    private final HoldingService holdingService;
    private final HoldingCalculationService holdingCalculationService;
    private final AccountService accountService;
    private final DividendPaymentCalculationService dividendPaymentCalculationService;
    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final SimpMessagingTemplate messagingTemplate;


    // Constructor
    public TransactionProcessorServiceImpl(TransactionService transactionService, HoldingService holdingService,
                                           HoldingCalculationService holdingCalculationService,
                                           AccountService accountService,
                                           DividendPaymentCalculationService dividendPaymentCalculationService,
                                           AccountRepository accountRepository, HoldingRepository holdingRepository,
                                           SimpMessagingTemplate messagingTemplate) {
        this.transactionService = transactionService;
        this.holdingService = holdingService;
        this.holdingCalculationService = holdingCalculationService;
        this.accountService = accountService;
        this.dividendPaymentCalculationService = dividendPaymentCalculationService;
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    @Transactional
    public TransactionDTO processTransaction(TransactionCreateRequest request) {

        // Retrieve account
        Account account = accountRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // --- Capture previous portfolio value ---
        BigDecimal holdingsValueBefore = holdingCalculationService.calculateTotalPortfolioValue(account.getAccountId());
        BigDecimal previousPortfolioValue = holdingsValueBefore.add(account.getAccountBalance());

        // Handle BUY transactions
        if (request.getTransactionType().name().equalsIgnoreCase("BUY")) {
            BigDecimal totalCost = request.getPricePerShare().multiply(request.getShareQuantity());

            // Check if account has enough balance
            if (account.getAccountBalance().compareTo(totalCost) < 0) {
                throw new IllegalArgumentException(
                        "Insufficient account balance. Need: A$" + totalCost.toPlainString() +
                                ", Have: A$" + account.getAccountBalance().toPlainString()
                );
            }

            // Deduct from balance
            account.setCashBalance(account.getCashBalance().subtract(totalCost));
        }
        // Handle SELL transactions
        else if (request.getTransactionType().name().equalsIgnoreCase("SELL")) {
            Optional<Holding> holdingOpt = holdingRepository.getHoldingByAccount_AccountIdAndStock_StockId(
                    request.getAccountId(),
                    request.getStockId()
            );

            if (holdingOpt.isEmpty()) {
                throw new IllegalArgumentException("No holding found for this stock");
            }

            Holding holding = holdingOpt.get();
            if (holding.getQuantity().compareTo(request.getShareQuantity()) < 0) {
                throw new IllegalArgumentException(
                        "Insufficient shares. Own: " + holding.getQuantity().toPlainString() +
                                ", Trying to sell: " + request.getShareQuantity().toPlainString()
                );
            }

            // Update holding and add proceeds to balance
            holdingService.updateHoldingAfterSale(holding, request.getShareQuantity(), request.getPricePerShare());
            BigDecimal saleProceeds = request.getPricePerShare().multiply(request.getShareQuantity());
            account.setCashBalance(account.getCashBalance().add(saleProceeds));
        }

        // 🔑 KEY: Save account with updated balance
        account = accountRepository.save(account);

        // Create transaction and update/create holding
        holdingService.updateOrCreateHoldingFromTransaction(request);
        TransactionDTO dto = transactionService.createTransaction(request);

        // --- Calculate and broadcast portfolio change ---
        BigDecimal currentPortfolioValue = holdingCalculationService.calculateTotalPortfolioValue(account.getAccountId());
        BigDecimal portfolioChange = currentPortfolioValue.subtract(previousPortfolioValue);

        WebSocketController.PortfolioUpdateMessage updateMessage = new WebSocketController.PortfolioUpdateMessage(
                account.getAccountId(),
                currentPortfolioValue,
                portfolioChange,
                LocalDateTime.now()
        );
        messagingTemplate.convertAndSend("/topic/portfolio/" + account.getAccountId(), updateMessage);

        return dto;
    }
}