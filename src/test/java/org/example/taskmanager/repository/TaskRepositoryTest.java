package org.example.taskmanager.repository;

import org.assertj.core.api.Assertions;
import org.example.taskmanager.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setName("Task");
        task.setDeadline(LocalDateTime.of(2024, 11, 17, 15, 0));
    }

    @Test
    void save(){

        Task savedTask = taskRepository.save(task);

        Assertions.assertThat(savedTask).isNotNull();
        Assertions.assertThat(savedTask.getId()).isNotNull();
        Assertions.assertThat(savedTask.getName()).isEqualTo(task.getName());
        Assertions.assertThat(savedTask.getDeadline()).isEqualTo(task.getDeadline());
    }

    @Test
    void findById(){
        Task savedTask = taskRepository.save(task);
        Task foundTask = taskRepository.findById(savedTask.getId()).orElse(null);

        Assertions.assertThat(foundTask).isNotNull();
        Assertions.assertThat(foundTask.getName()).isEqualTo(task.getName());
        Assertions.assertThat(foundTask.getDeadline()).isEqualTo(task.getDeadline());
    }

    @Test
    void findByID_NotFound(){
        Optional<Task> result = taskRepository.findById(-1L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void delete(){
        taskRepository.save(task);
        taskRepository.delete(task);

        Assertions.assertThat(taskRepository.findById(task.getId()).orElse(null)).isNull();
    }

    @Test
    void findAll(){
        taskRepository.save(task);
        List<Task> tasks = taskRepository.findAll();

        Assertions.assertThat(tasks).hasSize(1);
        Assertions.assertThat(tasks.get(0).getName()).isEqualTo(task.getName());
        Assertions.assertThat(tasks.get(0).getDeadline()).isEqualTo(task.getDeadline());
    }

    @Test
    void findAll_NotFound(){
        List<Task> tasks = taskRepository.findAll();
        Assertions.assertThat(tasks).isEmpty();
    }

    @Test
    void findByDeadline(){
        Task task1 = new Task();
        task1.setName("Task1");
        task1.setDeadline(LocalDateTime.now().plusDays(1));

        taskRepository.save(task);
        taskRepository.save(task1);
        List<Task> tasks = taskRepository.findByDeadlineBefore(LocalDateTime.now());

        Assertions.assertThat(tasks).hasSize(1);
        Assertions.assertThat(tasks.get(0).getName()).isEqualTo(task.getName());
        Assertions.assertThat(tasks.get(0).getDeadline()).isEqualTo(task.getDeadline());
    }

    @Test
    void findByDeadline_HaveNotExpiredTasks(){
        task.setDeadline(LocalDateTime.now().plusDays(1));
        taskRepository.save(task);
        List<Task> tasks = taskRepository.findByDeadlineBefore(LocalDateTime.now());

        Assertions.assertThat(tasks).isEmpty();
    }

}
