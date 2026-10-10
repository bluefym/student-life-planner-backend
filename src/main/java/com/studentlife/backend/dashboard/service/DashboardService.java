package com.studentlife.backend.dashboard.service;

import com.studentlife.backend.dashboard.dto.*;
//import com.studentlife.backend.expense.entity.ExpenseCategory;
import com.studentlife.backend.expense.repository.ExpenseRepository;
import com.studentlife.backend.habit.entity.Habit;
import com.studentlife.backend.habit.entity.HabitCheckIn;
import com.studentlife.backend.habit.repository.HabitCheckInRepository;
import com.studentlife.backend.habit.repository.HabitRepository;
import com.studentlife.backend.task.entity.Task;
import com.studentlife.backend.task.repository.TaskRepository;
import com.studentlife.backend.user.User;
import com.studentlife.backend.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final HabitRepository habitRepository;
    private final HabitCheckInRepository checkInRepository;
    private final TaskRepository taskRepository;

    public DashboardService(
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            HabitRepository habitRepository,
            HabitCheckInRepository checkInRepository,
            TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.habitRepository = habitRepository;
        this.checkInRepository = checkInRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"
            ));

        Long userId = user.getId();
        LocalDate today = LocalDate.now();

        // Monthly spending
        YearMonth month = YearMonth.from(today);
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        BigDecimal monthlyTotal =
            expenseRepository.sumMonthlyExpenses(
                userId, startDate, endDate
            );

        List<CategorySpending> categoryTotals =
            expenseRepository.sumByCategory(
                userId, startDate, endDate
            ).stream()
            .map(row -> new CategorySpending(
                String.valueOf(row[0]),
                (BigDecimal) row[1]
            ))
            .toList();

        SpendingSummary spending = new SpendingSummary(
            monthlyTotal,
            categoryTotals
        );

        // Today's active habits and check-ins
        List<Habit> activeHabits =
            habitRepository
                .findAllByUser_IdAndActiveTrueOrderByCreatedAtDesc(
                    userId
                );

        List<HabitCheckIn> allCheckIns =
            checkInRepository.findAllByHabit_User_Id(userId);

        Map<Long, Set<LocalDate>> datesByHabit =
            allCheckIns.stream().collect(
                Collectors.groupingBy(
                    checkIn -> checkIn.getHabit().getId(),
                    Collectors.mapping(
                        HabitCheckIn::getCheckInDate,
                        Collectors.toSet()
                    )
                )
            );

        List<HabitSummary> habitSummaries =
            activeHabits.stream().map(habit -> {
                Set<LocalDate> dates = datesByHabit.getOrDefault(
                    habit.getId(), Collections.emptySet()
                );

                int longest = 0;
                int run = 0;
                LocalDate previous = null;

                List<LocalDate> sortedDates = dates.stream()
                    .sorted()
                    .toList();

                for (LocalDate date : sortedDates) {
                    run = previous != null
                        && date.equals(previous.plusDays(1))
                        ? run + 1 : 1;

                    longest = Math.max(longest, run);
                    previous = date;
                }

                LocalDate streakEnd = dates.contains(today)
                    ? today : today.minusDays(1);

                int current = 0;

                if (dates.contains(streakEnd)) {
                    LocalDate cursor = streakEnd;
                    current = 1;

                    while (dates.contains(cursor.minusDays(1))) {
                        current++;
                        cursor = cursor.minusDays(1);
                    }
                }

                return new HabitSummary(
                    habit.getId(),
                    habit.getName(),
                    dates.contains(today),
                    current,
                    longest
                );
            }).toList();

        // Upcoming and overdue tasks
        List<Task> tasks =
            taskRepository.findAllByUser_IdOrderByDueDateAsc(userId);

        List<TaskSummary> overdueTasks = tasks.stream()
            .filter(task -> !task.isCompleted())
            .filter(task -> task.getDueDate() != null)
            .filter(task -> task.getDueDate().isBefore(today))
            .map(DashboardService::toTaskSummary)
            .toList();

        List<TaskSummary> upcomingTasks = tasks.stream()
            .filter(task -> !task.isCompleted())
            .filter(task -> task.getDueDate() == null
                || !task.getDueDate().isBefore(today))
            .sorted(Comparator.comparing(
                Task::getDueDate,
                Comparator.nullsLast(Comparator.naturalOrder())
            ))
            .limit(5)
            .map(DashboardService::toTaskSummary)
            .toList();

        return new DashboardResponse(
            today,
            spending,
            habitSummaries,
            upcomingTasks,
            overdueTasks
        );
    }

    private static TaskSummary toTaskSummary(Task task) {
        return new TaskSummary(
            task.getId(),
            task.getTitle(),
            task.getDueDate(),
            task.isCompleted(),
            !task.isCompleted()
                && task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDate.now())
        );
    }
}