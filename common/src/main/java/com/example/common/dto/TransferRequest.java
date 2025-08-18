package com.example.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
public record TransferRequest(
        @NotNull String transferId,
        @NotNull Long fromAccountId,
        @NotNull Long toAccountId,
        @NotNull @Positive BigDecimal amount
) {}