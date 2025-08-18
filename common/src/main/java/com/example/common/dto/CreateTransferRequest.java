package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransferRequest(@NotBlank Long fromAccountId, @NotBlank Long toAccountId, @NotNull BigDecimal amount) {}