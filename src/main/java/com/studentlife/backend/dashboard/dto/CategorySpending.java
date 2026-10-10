package com.studentlife.backend.dashboard.dto;

import java.math.BigDecimal;

public record CategorySpending(
    String category,
    BigDecimal amount
) {}