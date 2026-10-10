package com.studentlife.backend.task.service;

import com.studentlife.backend.task.dto.TaskRequest;
import com.studentlife.backend.task.dto.TaskResponse;
import com.studentlife.backend.task.entity.Task;
import com.studentlife.backend.task.repository.TaskRepository;
import com.studentlife.backend.user.User;
import com.studentlife.backend.user.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    private Task getOwnedTask(Long taskId, Long userId) {
        return taskRepository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Transactional
    public TaskResponse create(String email, TaskRequest request) {
        User user = getUser(email);

        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setCompleted(Boolean.TRUE.equals(request.completed()));
        task.setUser(user);

        return TaskResponse.from(taskRepository.save(task));
    }

    public List<TaskResponse> getAll(String email) {
        User user = getUser(email);

        return taskRepository
                .findAllByUser_IdOrderByDueDateAsc(user.getId())
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional
    public TaskResponse update(
            String email, Long taskId, TaskRequest request) {
        User user = getUser(email);
        Task task = getOwnedTask(taskId, user.getId());

        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());

        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse setCompleted(
            String email, Long taskId, boolean completed) {
        User user = getUser(email);
        Task task = getOwnedTask(taskId, user.getId());

        task.setCompleted(completed);
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void delete(String email, Long taskId) {
        User user = getUser(email);
        Task task = getOwnedTask(taskId, user.getId());

        taskRepository.delete(task);
    }
}
