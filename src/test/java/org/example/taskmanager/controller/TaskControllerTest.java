package org.example.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.taskmanager.entity.Task;
import org.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;


    private ObjectMapper objectMapper;
    private Task task;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        task = new Task(1L, "Test Task", LocalDateTime.of(2024, 11, 17, 15, 0));
    }

    @Test
    void shouldCreateTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deadline").value("17-11-2024 15:00:00"));
        verify(taskService, times(1)).createTask(task);
    }

    @Test
    void shouldGetTask() throws Exception {
        when(taskService.getTask(1L)).thenReturn(task);

        mockMvc.perform(get("/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deadline").value("17-11-2024 15:00:00"));

        verify(taskService, times(1)).getTask(1L);
    }

    @Test
    void shouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTaskById(1L);

        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTaskById(1L);
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Task"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].deadline").value("17-11-2024 15:00:00"));

        verify(taskService, times(1)).getAllTasks();
    }
}
