package com.studentlife.backend.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record SpendingSummary(
    BigDecimal monthlyTotal,
    List<CategorySpending> categoryTotals
) {}