package com.example.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountRequest(@NotNull @Positive BigDecimal initialBalance) { }