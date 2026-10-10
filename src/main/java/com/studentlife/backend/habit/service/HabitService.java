package com.studentlife.backend.habit.service;

import com.studentlife.backend.habit.dto.*;
import com.studentlife.backend.habit.entity.Habit;
import com.studentlife.backend.habit.entity.HabitCheckIn;
import com.studentlife.backend.habit.repository.HabitCheckInRepository;
import com.studentlife.backend.habit.repository.HabitRepository;
import com.studentlife.backend.user.User;
import com.studentlife.backend.user.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository checkInRepository;
    private final UserRepository userRepository;

    public HabitService(
        HabitRepository habitRepository,
        HabitCheckInRepository checkInRepository,
        UserRepository userRepository
    ) {
        this.habitRepository = habitRepository;
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"
            ));
    }

    private Habit getOwnedHabit(Long habitId, Long userId) {
        return habitRepository.findByIdAndUser_Id(habitId, userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Habit not found"
            ));
    }

    @Transactional
    public HabitResponse create(String email, HabitRequest request) {
        User user = getUser(email);

        Habit habit = new Habit();
        habit.setName(request.name().trim());
        habit.setDescription(request.description());
        habit.setUser(user);
        habit.setActive(true);

        return HabitResponse.from(habitRepository.save(habit));
    }

    @Transactional(readOnly = true)
    public List<HabitResponse> getAll(String email) {
        User user = getUser(email);

        return habitRepository
            .findAllByUser_IdOrderByCreatedAtDesc(user.getId())
            .stream()
            .map(HabitResponse::from)
            .toList();
    }

    @Transactional
    public HabitResponse update(
        String email,
        Long habitId,
        HabitRequest request
    ) {
        User user = getUser(email);
        Habit habit = getOwnedHabit(habitId, user.getId());

        habit.setName(request.name().trim());
        habit.setDescription(request.description());

        return HabitResponse.from(habitRepository.save(habit));
    }

    @Transactional
public void delete(String email, Long habitId) {
    User user = getUser(email);
    Habit habit = getOwnedHabit(habitId, user.getId());

    checkInRepository.deleteAllByHabit_Id(habitId);
    habitRepository.delete(habit);
}

    @Transactional
    public HabitResponse setActive(
        String email,
        Long habitId,
        boolean active
    ) {
        User user = getUser(email);
        Habit habit = getOwnedHabit(habitId, user.getId());

        habit.setActive(active);

        return HabitResponse.from(habitRepository.save(habit));
    }

    @Transactional
    public String checkIn(
        String email,
        Long habitId,
        CheckInRequest request
    ) {
        User user = getUser(email);
        Habit habit = getOwnedHabit(habitId, user.getId());

        if (!habit.isActive()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "This habit is inactive"
            );
        }

        if (checkInRepository.existsByHabit_IdAndCheckInDate(
            habitId, request.date()
        )) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This habit has already been checked in for that date"
            );
        }

        HabitCheckIn checkIn = new HabitCheckIn();
        checkIn.setHabit(habit);
        checkIn.setCheckInDate(request.date());

        try {
            checkInRepository.saveAndFlush(checkIn);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This habit has already been checked in for that date"
            );
        }

        return "Habit checked in successfully for " + request.date();
    }

    @Transactional(readOnly = true)
    public HabitStreakResponse getStreak(
        String email,
        Long habitId
    ) {
        User user = getUser(email);
        Habit habit = getOwnedHabit(habitId, user.getId());

        List<HabitCheckIn> checkIns =
            checkInRepository.findAllByHabit_IdOrderByCheckInDateAsc(
                habitId
            );

        List<LocalDate> dates = checkIns.stream()
            .map(HabitCheckIn::getCheckInDate)
            .distinct()
            .sorted()
            .toList();

        int longest = 0;
        int run = 0;
        LocalDate previous = null;

        for (LocalDate date : dates) {
            if (previous != null && date.equals(previous.plusDays(1))) {
                run++;
            } else {
                run = 1;
            }

            longest = Math.max(longest, run);
            previous = date;
        }

        LocalDate today = LocalDate.now();
        LocalDate currentEnd = dates.contains(today)
            ? today
            : today.minusDays(1);

        int current = 0;

        if (dates.contains(currentEnd)) {
            current = 1;
            LocalDate cursor = currentEnd;

            while (dates.contains(cursor.minusDays(1))) {
                current++;
                cursor = cursor.minusDays(1);
            }
        }

        return new HabitStreakResponse(
            habit.getId(),
            habit.getName(),
            current,
            longest
        );
    }
}