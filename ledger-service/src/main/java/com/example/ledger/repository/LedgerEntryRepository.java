package com.example.ledger.repository;

import com.example.common.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    boolean existsByTransferId(String transferId);
}