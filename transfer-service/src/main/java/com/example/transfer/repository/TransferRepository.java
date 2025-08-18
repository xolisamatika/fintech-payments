package com.example.transfer.repository;

import com.example.common.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface TransferRepository  extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByIdempotencyKey(String key);
    Optional<Transfer> findByTransferId(String key);
    @Modifying
    @Query("DELETE FROM Transfer r WHERE r.createdAt < :expiry")
    int  deleteExpired(@Param("expiry") Instant expiry);
}
