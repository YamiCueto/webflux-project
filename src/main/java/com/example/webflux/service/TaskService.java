package com.example.webflux.service;

import com.example.webflux.exception.TaskException;
import com.example.webflux.exception.TaskNotFoundException;
import com.example.webflux.model.Task;
import com.example.webflux.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Flux<Task> getAllTasks() {
        return taskRepository.findAll()
                .onErrorMap(e -> new TaskException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error retrieving tasks", e));
    }

    public Mono<Task> createTask(Task task) {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            return Mono.error(new TaskException(HttpStatus.BAD_REQUEST, "Task description cannot be empty"));
        }
        return taskRepository.save(task);
    }

    public Flux<Task> processTasksInParallel(Flux<Task> tasks) {
        return tasks
            .flatMap(task -> 
                Mono.just(task)
                    .map(this::enrichTask)
                    .onErrorResume(e -> Mono.error(new TaskException(HttpStatus.INTERNAL_SERVER_ERROR, 
                        String.format("Error processing task: %s", e.getMessage()), e)))
                    .subscribeOn(Schedulers.boundedElastic())
            );
    }

    // Ejemplo de operación con delay simulando procesamiento asíncrono
    public Mono<Task> processTaskWithDelay(Task task) {
        return Mono.just(task)
                .delayElement(Duration.ofMillis(100))
                .map(this::enrichTask);
    }

    public Mono<Task> processTaskWithRetry(String taskId) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(taskId)))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter(throwable -> !(throwable instanceof TaskNotFoundException)))
                .map(this::enrichTask);
    }

    public Mono<Task> processTaskWithTimeout(String taskId) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(taskId)))
                .timeout(Duration.ofSeconds(5))
                .onErrorMap(throwable -> {
                    if (throwable instanceof TaskNotFoundException) {
                        return throwable;
                    }
                    return new TaskException(HttpStatus.REQUEST_TIMEOUT, "Operation timed out", throwable);
                })
                .map(this::enrichTask);
    }

    // Ejemplo de operaciones en batch
    public Flux<Task> processBatchTasks(Flux<Task> tasks) {
        return tasks.buffer(10)
                .flatMap(taskList -> Flux.fromIterable(taskList)
                        .map(this::enrichTask)
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    private Task enrichTask(Task task) {
        if (task == null) {
            throw new TaskException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot process null task");
        }
        task.setStatus(task.getStatus() != null ? task.getStatus() : "PROCESSED");
        return task;
    }
}
