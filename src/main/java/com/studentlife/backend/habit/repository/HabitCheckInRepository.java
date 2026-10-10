package com.studentlife.backend.habit.repository;
import java.util.Collection;
import com.studentlife.backend.habit.entity.HabitCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCheckInRepository
        extends JpaRepository<HabitCheckIn, Long> {

    boolean existsByHabit_IdAndCheckInDate(Long habitId, LocalDate date);
    List<HabitCheckIn> findAllByHabit_IdInAndCheckInDate(
    Collection<Long> habitIds,
    LocalDate checkInDate
);
List<HabitCheckIn> findAllByHabit_User_Id(Long userId);

List<HabitCheckIn> findAllByHabit_User_IdAndCheckInDate(
    Long userId,
    LocalDate date
);
    Optional<HabitCheckIn> findByHabit_IdAndCheckInDate(
        Long habitId,
        LocalDate date
    );

    List<HabitCheckIn> findAllByHabit_IdOrderByCheckInDateAsc(
        Long habitId
    );
    void deleteAllByHabit_Id(Long habitId);
}