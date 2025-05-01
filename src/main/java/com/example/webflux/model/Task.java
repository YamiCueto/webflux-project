package com.example.webflux.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class Task {
    @Id
    @Column("id")
    private Long id;
    private String description;
    private String status;
    private int priority;
}
