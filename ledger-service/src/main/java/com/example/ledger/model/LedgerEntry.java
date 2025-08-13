package com.example.ledger.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ledger_entries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"transfer_id", "type", "status"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String transferId;
    @Column(nullable = false)
    private Long accountId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    private LocalDateTime createdAt;

    public enum EntryType { DEBIT, CREDIT }
}
