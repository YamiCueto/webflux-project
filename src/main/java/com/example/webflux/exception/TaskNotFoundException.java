package com.example.webflux.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends TaskException {
    
    public TaskNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
    
    public TaskNotFoundException(Long taskId) {
        super(HttpStatus.NOT_FOUND, String.format("Task with id %d not found", taskId));
    }
}
