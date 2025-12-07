package com.joelcode.personalinvestmentportfoliotracker.services.mapping;

import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.transaction.TransactionDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    // Convert transaction creation request DTO to entity
    public static Transaction toEntity(TransactionCreateRequest request) {
        Transaction transaction = new Transaction();
        transaction.setShareQuantity(request.getShareQuantity());
        transaction.setPricePerShare(request.getPricePerShare());
        // Ensure non-null commission (default to zero)
        transaction.setCommission(java.math.BigDecimal.ZERO);
        // Set transaction type
        transaction.setTransactionType(request.getTransactionType());
        // Initialize and set associations by ID only
        if (request.getAccountId() != null) {
            com.joelcode.personalinvestmentportfoliotracker.entities.Account account = new com.joelcode.personalinvestmentportfoliotracker.entities.Account();
            account.setAccountId(request.getAccountId());
            transaction.setAccount(account);
        }
        if (request.getStockId() != null) {
            com.joelcode.personalinvestmentportfoliotracker.entities.Stock stock = new com.joelcode.personalinvestmentportfoliotracker.entities.Stock();
            stock.setStockId(request.getStockId());
            transaction.setStock(stock);
        }
        return transaction;
    }

    // Convert transaction entity to transaction response DTO
    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) return null;
        return new TransactionDTO(
                transaction.getTransactionId(),
                transaction.getStock() != null ? transaction.getStock().getStockId() : null,
                transaction.getAccount() != null ? transaction.getAccount().getAccountId() : null,
                transaction.getShareQuantity(),
                transaction.getPricePerShare(),
                transaction.getTransactionType(),
                transaction.getCreatedAt()
        );
    }
}
