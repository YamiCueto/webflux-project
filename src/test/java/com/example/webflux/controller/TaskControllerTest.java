package com.example.webflux.controller;

import com.example.webflux.model.Task;
import com.example.webflux.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setDescription("Test Task");
        task.setStatus("PENDING");
        task.setPriority(1);
    }

    @Test
    void whenGetAllTasksShouldReturnTaskList() {
        Mockito.when(taskService.getAllTasks())
                .thenReturn(Flux.just(task));

        webTestClient.get()
                .uri("/api/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(1)
                .contains(task);
    }

    @Test
    void whenCreateTaskShouldReturnCreatedTask() {
        Mockito.when(taskService.createTask(Mockito.any(Task.class)))
                .thenReturn(Mono.just(task));

        webTestClient.post()
                .uri("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .isEqualTo(task);
    }
}
