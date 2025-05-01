package com.example.webflux.service;

import com.example.webflux.model.Task;
import com.example.webflux.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
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

    // Ejemplo de operación no bloqueante básica
    public Mono<Task> createTask(Task task) {
        return taskRepository.save(task);
    }

    // Ejemplo de operaciones en paralelo usando flatMap
    public Flux<Task> processTasksInParallel(Flux<Task> tasks) {
        return tasks.flatMap(task -> 
            Mono.just(task)
                .map(this::enrichTask)
                .subscribeOn(Schedulers.boundedElastic())
        );
    }

    // Ejemplo de operación con delay simulando procesamiento asíncrono
    public Mono<Task> processTaskWithDelay(Task task) {
        return Mono.just(task)
                .delayElement(Duration.ofMillis(100))
                .map(this::enrichTask);
    }

    // Ejemplo de operación con retry
    public Mono<Task> processTaskWithRetry(String taskId) {
        return taskRepository.findById(taskId)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .map(this::enrichTask);
    }

    // Ejemplo de operación con timeout
    public Mono<Task> processTaskWithTimeout(String taskId) {
        return taskRepository.findById(taskId)
                .timeout(Duration.ofSeconds(5))
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
        task.setStatus("PROCESSED");
        return task;
    }
}
