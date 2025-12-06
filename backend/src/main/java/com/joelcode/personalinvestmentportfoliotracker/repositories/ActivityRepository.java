package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByUser_UserId(UUID userId);

    Page<Activity> findByUser_UserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Activity> findByUser_UserIdAndTypeOrderByCreatedAtDesc(UUID userId, String type, Pageable pageable);

    List<Activity> findByUser_UserIdAndCreatedAtBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
}
