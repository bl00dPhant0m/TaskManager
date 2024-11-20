package org.example.taskmanager.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager.entity.Task;
import org.example.taskmanager.service.TaskService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("customTaskScheduler")
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class TaskScheduler {
    private final TaskService taskService;

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    @Async
    public void checkTasksWithExpiredDeadline() {
        List<Task> tasks = taskService.getExpiredTasks();

        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        tasks.stream()
                .map(task -> task + " с истекшим дедлайном")
                .forEach(log::info);
    }
}
