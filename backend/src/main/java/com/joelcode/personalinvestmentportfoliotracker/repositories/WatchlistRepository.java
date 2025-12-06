package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import com.joelcode.personalinvestmentportfoliotracker.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, UUID> {

    List<Watchlist> findByUser(User user);

    List<Watchlist> findByUser_UserId(UUID userId);

    Optional<Watchlist> findByUser_UserIdAndStock_StockId(UUID userId, UUID stockId);

    void deleteByUser_UserIdAndStock_StockId(UUID userId, UUID stockId);

    boolean existsByUser_UserIdAndStock_StockId(UUID userId, UUID stockId);
}
