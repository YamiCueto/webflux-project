package com.example.webflux.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskException extends ResponseStatusException {
    
    public TaskException(HttpStatus status, String message) {
        super(status, message);
    }

    public TaskException(HttpStatus status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
