package com.example.common.dto;

import java.math.BigDecimal;

public record AccountResponse(Long id, BigDecimal balance, Long version) { }
