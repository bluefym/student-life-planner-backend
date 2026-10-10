package com.studentlife.backend.expense.dto;

import com.studentlife.backend.expense.entity.Expense;
import com.studentlife.backend.expense.entity.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
    Long id,
    BigDecimal amount,
    ExpenseCategory category,
    String description,
    LocalDate expenseDate,
    LocalDateTime createdAt
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getAmount(),
            expense.getCategory(),
            expense.getDescription(),
            expense.getExpenseDate(),
            expense.getCreatedAt()
        );
    }
}