package com.studentlife.backend.expense.dto;

import com.studentlife.backend.expense.entity.ExpenseCategory;

import java.math.BigDecimal;

public record CategoryTotalResponse(
    ExpenseCategory category,
    BigDecimal total
) {
}