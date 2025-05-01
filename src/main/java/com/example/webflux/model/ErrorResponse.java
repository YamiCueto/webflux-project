package com.example.webflux.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String path;
    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;
    private String requestId;
}
