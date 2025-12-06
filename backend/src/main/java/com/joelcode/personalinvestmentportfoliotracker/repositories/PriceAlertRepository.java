package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, UUID> {

    List<PriceAlert> findByUser_UserId(UUID userId);

    List<PriceAlert> findByUser_UserIdAndIsActive(UUID userId, Boolean isActive);

    List<PriceAlert> findByStock_StockId(UUID stockId);

    void deleteByUser_UserIdAndAlertId(UUID userUserId, UUID alertId);
}
