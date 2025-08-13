package com.example.common.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id @GeneratedValue
    private Long id;

    @Version
    private Long version; // optimistic locking

    private BigDecimal balance;
}
