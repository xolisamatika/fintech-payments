package com.example.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ledger_entries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"transfer_id", "type"})
        }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String transferId;
    @Column(nullable = false)
    private Long accountId;
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum EntryType { DEBIT, CREDIT }
}
