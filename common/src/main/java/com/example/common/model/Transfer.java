package com.example.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "transfers", uniqueConstraints = @UniqueConstraint(columnNames = "idempotencyKey"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private String transferId;
    @Column(nullable = false)
    private Instant createdAt;
}