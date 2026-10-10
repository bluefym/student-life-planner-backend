package com.studentlife.backend.expense.service;

import com.studentlife.backend.expense.dto.CategoryTotalResponse;
import com.studentlife.backend.expense.dto.ExpenseRequest;
import com.studentlife.backend.expense.dto.ExpenseResponse;
import com.studentlife.backend.expense.entity.Expense;
import com.studentlife.backend.expense.entity.ExpenseCategory;
import com.studentlife.backend.expense.repository.ExpenseRepository;
import com.studentlife.backend.user.User;
import com.studentlife.backend.user.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
        ExpenseRepository expenseRepository,
        UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authenticated user not found"
            ));
    }

    private Expense getExpense(Long id, Long userId) {
        return expenseRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Expense not found"
            ));
    }

    private void applyRequest(
        Expense expense,
        ExpenseRequest request
    ) {
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDescription(request.description());
        expense.setExpenseDate(request.expenseDate());
    }

    @Transactional
    public ExpenseResponse create(
        String email,
        ExpenseRequest request
    ) {
        User user = getUser(email);

        Expense expense = new Expense();
        expense.setUser(user);
        applyRequest(expense, request);

        Expense saved = expenseRepository.save(expense);

        return ExpenseResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAll(
        String email,
        int page,
        int size,
        String sortBy,
        String direction
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Page must be non-negative and size must be between 1 and 100"
            );
        }

        Set<String> allowedSortFields = Set.of(
            "expenseDate",
            "amount",
            "category",
            "createdAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unsupported sort field"
            );
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Direction must be ASC or DESC"
            );
        }

        User user = getUser(email);

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortDirection, sortBy)
        );

        return expenseRepository
            .findAllByUser_Id(user.getId(), pageable)
            .map(ExpenseResponse::from);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getById(String email, Long id) {
        User user = getUser(email);

        return ExpenseResponse.from(
            getExpense(id, user.getId())
        );
    }

    @Transactional
    public ExpenseResponse update(
        String email,
        Long id,
        ExpenseRequest request
    ) {
        User user = getUser(email);
        Expense expense = getExpense(id, user.getId());

        applyRequest(expense, request);

        return ExpenseResponse.from(
            expenseRepository.save(expense)
        );
    }

    @Transactional
    public void delete(String email, Long id) {
        User user = getUser(email);
        Expense expense = getExpense(id, user.getId());

        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyTotal(
        String email,
        int year,
        int month
    ) {
        YearMonth yearMonth = validateMonth(year, month);
        User user = getUser(email);

        return expenseRepository.sumMonthlyExpenses(
            user.getId(),
            yearMonth.atDay(1),
            yearMonth.plusMonths(1).atDay(1)
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryTotalResponse> getCategoryTotals(
        String email,
        int year,
        int month
    ) {
        YearMonth yearMonth = validateMonth(year, month);
        User user = getUser(email);

        List<Object[]> rows = expenseRepository.sumByCategory(
            user.getId(),
            yearMonth.atDay(1),
            yearMonth.plusMonths(1).atDay(1)
        );

        List<CategoryTotalResponse> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(new CategoryTotalResponse(
                (ExpenseCategory) row[0],
                (BigDecimal) row[1]
            ));
        }

        return result;
    }

    private YearMonth validateMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid year or month"
            );
        }
    }
}