package com.example.webflux.repository;

import com.example.webflux.model.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface TaskRepository extends R2dbcRepository<Task, String> {
    Flux<Task> findByStatus(String status);
}
