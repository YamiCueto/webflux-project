package com.example.webflux.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class Task {
    @Id
    private String id;
    private String description;
    private String status;
    private int priority;
}
