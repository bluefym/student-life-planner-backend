package com.studentlife.backend.habit.repository;

import com.studentlife.backend.habit.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Habit> findByIdAndUser_Id(Long id, Long userId);
    List<Habit> findAllByUser_IdAndActiveTrueOrderByCreatedAtDesc(
    Long userId
);
}