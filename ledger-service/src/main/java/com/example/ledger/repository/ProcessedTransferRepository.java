package com.example.ledger.repository;

import com.example.ledger.model.ProcessedTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessedTransferRepository extends JpaRepository<ProcessedTransfer, String> {
    Optional<ProcessedTransfer> findByTransferId(String transferId);
}