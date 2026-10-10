package com.studentlife.backend.task.repository;

import com.studentlife.backend.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUser_IdOrderByDueDateAsc(Long userId);

    Optional<Task> findByIdAndUser_Id(Long id, Long userId);
}