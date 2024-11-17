package org.example.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager.entity.Task;
import org.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task getTask(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " not found") );
    }

    @Override
    public void deleteTaskById(long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getExpiredTasks() {
        return taskRepository.findByDeadlineBefore(LocalDateTime.now());
    }
}
