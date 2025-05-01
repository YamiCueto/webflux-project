package com.example.webflux.config;

import com.example.webflux.repository.TaskRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TaskHealthIndicator implements HealthIndicator {
    
    private final TaskRepository taskRepository;
    
    public TaskHealthIndicator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @Override
    public Health health() {
        try {
            Long taskCount = taskRepository.count().block();
            return Health.up()
                    .withDetail("taskCount", taskCount)
                    .withDetail("status", "Task service is operational")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Task service is not operational")
                    .build();
        }
    }
}
