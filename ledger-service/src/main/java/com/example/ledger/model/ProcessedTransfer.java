package com.example.ledger.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "processed_transfers", uniqueConstraints = @UniqueConstraint(columnNames = "transferId"))
@Data
@RequiredArgsConstructor
public class ProcessedTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transferId;

    @Enumerated(EnumType.STRING)
    private Status status; // "SUCCESS" | "PENDING" | "FAILED"

    @Column
    private String error; // optional error message

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum Status { PENDING, SUCCESS, FAILED }
}
