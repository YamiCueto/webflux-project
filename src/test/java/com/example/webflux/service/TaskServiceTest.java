package com.example.webflux.service;

import com.example.webflux.exception.TaskException;
import com.example.webflux.exception.TaskNotFoundException;
import com.example.webflux.model.Task;
import com.example.webflux.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
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
        when(taskRepository.findAll()).thenReturn(Flux.just(task));

        StepVerifier.create(taskService.getAllTasks())
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void whenCreateTaskWithValidDataShouldSucceed() {
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));

        StepVerifier.create(taskService.createTask(task))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void whenCreateTaskWithEmptyDescriptionShouldFail() {
        Task emptyTask = new Task();
        emptyTask.setDescription("");

        StepVerifier.create(taskService.createTask(emptyTask))
                .expectError(TaskException.class)
                .verify();
    }

    @Test
    void whenProcessTaskWithRetryShouldSucceed() {
        when(taskRepository.findById("1")).thenReturn(Mono.just(task));

        StepVerifier.create(taskService.processTaskWithRetry("1"))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void whenProcessTaskWithRetryAndNotFoundShouldFail() {
        when(taskRepository.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(taskService.processTaskWithRetry("999"))
                .expectError(TaskNotFoundException.class)
                .verify();
    }

    @Test
    void whenProcessTaskWithTimeoutShouldSucceed() {
        when(taskRepository.findById("1")).thenReturn(Mono.just(task));

        StepVerifier.create(taskService.processTaskWithTimeout("1"))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void whenProcessTasksInParallelShouldSucceed() {
        lenient().when(taskRepository.findAll()).thenReturn(Flux.just(task));

        StepVerifier.create(taskService.processTasksInParallel(Flux.just(task)))
                .expectNext(task)
                .verifyComplete();
    }
}

