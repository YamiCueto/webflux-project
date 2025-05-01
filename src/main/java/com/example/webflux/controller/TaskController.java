package com.example.webflux.controller;

import com.example.webflux.model.Task;
import com.example.webflux.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks reactively")
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Creates a new task and returns it")
    @PostMapping
    public Mono<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @Operation(summary = "Get task by ID with timeout", description = "Retrieves a task by its ID with a timeout of 5 seconds")
    @GetMapping("/{id}")
    public Mono<Task> getTaskWithTimeout(@PathVariable String id) {
        return taskService.processTaskWithTimeout(id);
    }

    @Operation(summary = "Get task by ID with retry", description = "Retrieves a task by its ID with retry capability")
    @GetMapping("/{id}/retry")
    public Mono<Task> getTaskWithRetry(@PathVariable String id) {
        return taskService.processTaskWithRetry(id);
    }

    @Operation(summary = "Process tasks in batch", description = "Processes multiple tasks in batch mode")
    @PostMapping("/batch")
    public Flux<Task> processBatch(@RequestBody Flux<Task> tasks) {
        return taskService.processBatchTasks(tasks);
    }

    @Operation(summary = "Stream tasks processing", description = "Processes tasks in parallel and returns a stream of results")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Task> streamTasks(@RequestBody Flux<Task> tasks) {
        return taskService.processTasksInParallel(tasks);
    }
}
