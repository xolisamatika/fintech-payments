package com.example.common.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="transfers", uniqueConstraints = @UniqueConstraint(columnNames = "clientKey"))
@Data
public class Transfer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String clientKey; // Idempotency-Key

    @Column(nullable=false, unique=true)
    private String transferId; // server-assigned UUID

    @Column(nullable=false)
    private Long fromAccountId;

    @Column(nullable=false)
    private Long toAccountId;

    @Column(nullable=false)
    private BigDecimal amount;

    @Column(nullable=false)
    private String status; // NEW|SUCCESS|FAILED

    @Column
    private String lastError;

    @Column(nullable=false, updatable=false)
    private Instant createdAt = Instant.now();
}