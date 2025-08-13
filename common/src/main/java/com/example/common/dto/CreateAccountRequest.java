package com.example.common.dto;

import java.math.BigDecimal;

public record CreateAccountRequest(BigDecimal initialBalance) { }