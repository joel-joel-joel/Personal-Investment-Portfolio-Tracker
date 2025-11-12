package com.joelcode.personalinvestmentportfoliotracker.repositories;

import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Find specific Id
    Optional<User> findByUserId(UUID userId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUserID(UUID userid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


    // Filter list of users
    List<User> findByCreatedAtAfter(LocalDateTime createdAtAfter);

    List<User> findByUsernameContainingIgnoreCase(String usernameFragment);

    List<User> findWithinDateRange(LocalDateTime start, LocalDateTime end);
}
