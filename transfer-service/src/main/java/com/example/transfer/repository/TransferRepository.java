package com.example.transfer.repository;

import com.example.common.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository  extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByTransferId(String id);
    Optional<Transfer> findByClientKey(String clientKey);
}
