package com.studentlife.backend.expense.repository;

import com.studentlife.backend.expense.entity.Expense;
import com.studentlife.backend.expense.entity.ExpenseCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findAllByUser_Id(Long userId, Pageable pageable);

    Optional<Expense> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
          AND e.expenseDate >= :startDate
          AND e.expenseDate < :endDate
        """)
    BigDecimal sumMonthlyExpenses(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT e.category, SUM(e.amount)
        FROM Expense e
        WHERE e.user.id = :userId
          AND e.expenseDate >= :startDate
          AND e.expenseDate < :endDate
        GROUP BY e.category
        ORDER BY e.category
        """)
    List<Object[]> sumByCategory(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}