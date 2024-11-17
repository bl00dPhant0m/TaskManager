package org.example.taskmanager.service;

import org.example.taskmanager.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task getTask(long id);
    void deleteTaskById(long id);
    List<Task> getAllTasks();
    List<Task> getExpiredTasks();
}
