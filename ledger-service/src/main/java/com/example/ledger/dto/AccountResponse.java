package com.example.ledger.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountResponse {
    private Long accountId;
    private BigDecimal balance;
    private Long version;
}
