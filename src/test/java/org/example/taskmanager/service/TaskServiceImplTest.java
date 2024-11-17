package org.example.taskmanager.service;

import org.example.taskmanager.entity.Task;
import org.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private long taskId = 1L;

    @BeforeEach
    public void setUp() {
        task = new Task(taskId, "Test Task", LocalDateTime.now().plusDays(1));
    }

    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getName());
        assertEquals(taskId, createdTask.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testGetTask() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTask(taskId);

        assertNotNull(foundTask);
        assertEquals(taskId, foundTask.getId());
        assertEquals("Test Task", foundTask.getName());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    public void testGetTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTask(taskId));
        assertEquals("Task with id " + taskId + " not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    public void testDeleteTaskById() {
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTaskById(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    public void testDeleteTaskByIdNotFound() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskService.deleteTaskById(taskId));
        assertEquals("Task with id " + taskId + " not found", exception.getMessage());
    }

    @Test
    public void testGetAllTasks() {
        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> allTasks = taskService.getAllTasks();

        assertNotNull(allTasks);
        assertEquals(1, allTasks.size());
        assertEquals("Test Task", allTasks.get(0).getName());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testGetExpiredTasks() {
        Task expiredTask = new Task(2L, "Expired Task", LocalDateTime.now().minusDays(1)); // Task with past deadline
        List<Task> expiredTasks = List.of(expiredTask);

        when(taskRepository.findByDeadlineBefore(any(LocalDateTime.class))).thenReturn(expiredTasks);

        List<Task> tasks = taskService.getExpiredTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Expired Task", tasks.get(0).getName());
        verify(taskRepository, times(1)).findByDeadlineBefore(any(LocalDateTime.class));
    }
}
