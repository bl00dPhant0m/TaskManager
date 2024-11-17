package org.example.taskmanager.scheduler;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.example.taskmanager.entity.Task;
import org.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableAsync
@ExtendWith(MockitoExtension.class)
class TaskSchedulerTest {

    @MockBean
    private TaskService taskService;

    @Autowired
    private TaskScheduler taskScheduler;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // Получаем логгер для класса TaskScheduler
        Logger logger = (Logger) LoggerFactory.getLogger(TaskScheduler.class);

        // Создаем ListAppender для перехвата логов
        listAppender = new ListAppender<>();
        listAppender.start();

        // Добавляем ListAppender в логгер
        logger.addAppender(listAppender);
    }

    @Test
    void testCheckTasksWithExpiredDeadline_noTasks() {
        when(taskService.getExpiredTasks()).thenReturn(Collections.emptyList());
        taskScheduler.checkTasksWithExpiredDeadline();

        assertTrue(listAppender.list.isEmpty());
        verify(taskService, times(1)).getExpiredTasks();
    }

    @Test
    void testCheckTasksWithExpiredDeadline_withExpiredTasks() throws InterruptedException {
        Task task1 = new Task(1L, "Task 1", LocalDateTime.now().minusDays(1));
        Task task2 = new Task(2L, "Task 2", LocalDateTime.now().minusDays(2));
        List<Task> expiredTasks = Arrays.asList(task1, task2);
        when(taskService.getExpiredTasks()).thenReturn(expiredTasks);

        taskScheduler.checkTasksWithExpiredDeadline();
        Thread.sleep(1000);

        assertTrue(listAppender.list.stream().anyMatch(event -> event.getMessage().contains(task1 + " с истекшим дедлайном")));
        assertTrue(listAppender.list.stream().anyMatch(event -> event.getMessage().contains(task2 + " с истекшим дедлайном")));
    }

    @Test
    void testCheckTasksWithExpiredDeadline_taskServiceReturnsNull() throws InterruptedException {
        when(taskService.getExpiredTasks()).thenReturn(null);
        taskScheduler.checkTasksWithExpiredDeadline();
        Thread.sleep(1000);

        assertTrue(listAppender.list.isEmpty());
        verify(taskService, times(1)).getExpiredTasks();
    }
}
