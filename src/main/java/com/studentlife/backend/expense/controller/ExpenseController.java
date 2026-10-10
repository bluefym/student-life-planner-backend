package com.studentlife.backend.expense.controller;

import com.studentlife.backend.expense.dto.CategoryTotalResponse;
import com.studentlife.backend.expense.dto.ExpenseRequest;
import com.studentlife.backend.expense.dto.ExpenseResponse;
import com.studentlife.backend.expense.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(
        Authentication authentication,
        @Valid @RequestBody ExpenseRequest request
    ) {
        return expenseService.create(
            authentication.getName(),
            request
        );
    }

    @GetMapping
    public Page<ExpenseResponse> getAll(
        Authentication authentication,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "expenseDate") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        return expenseService.getAll(
            authentication.getName(),
            page,
            size,
            sortBy,
            direction
        );
    }

    @GetMapping("/{id}")
    public ExpenseResponse getById(
        Authentication authentication,
        @PathVariable Long id
    ) {
        return expenseService.getById(
            authentication.getName(),
            id
        );
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(
        Authentication authentication,
        @PathVariable Long id,
        @Valid @RequestBody ExpenseRequest request
    ) {
        return expenseService.update(
            authentication.getName(),
            id,
            request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        Authentication authentication,
        @PathVariable Long id
    ) {
        expenseService.delete(
            authentication.getName(),
            id
        );
    }

    @GetMapping("/summary/total")
    public BigDecimal getMonthlyTotal(
        Authentication authentication,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return expenseService.getMonthlyTotal(
            authentication.getName(),
            year,
            month
        );
    }

    @GetMapping("/summary/categories")
    public List<CategoryTotalResponse> getCategoryTotals(
        Authentication authentication,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return expenseService.getCategoryTotals(
            authentication.getName(),
            year,
            month
        );
    }
}