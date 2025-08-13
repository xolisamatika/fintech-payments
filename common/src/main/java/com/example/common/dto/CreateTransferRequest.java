package com.example.common.dto;

import java.math.BigDecimal;

public record CreateTransferRequest(Long fromAccountId, Long toAccountId, BigDecimal amount) {}