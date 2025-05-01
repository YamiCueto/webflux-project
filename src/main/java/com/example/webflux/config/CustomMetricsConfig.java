package com.example.webflux.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomMetricsConfig {
    
    @Bean
    public Counter taskCreationCounter(MeterRegistry registry) {
        return Counter.builder("app.tasks.created")
                .description("Number of tasks created")
                .register(registry);
    }

    @Bean
    public Counter taskCompletionCounter(MeterRegistry registry) {
        return Counter.builder("app.tasks.completed")
                .description("Number of tasks marked as completed")
                .register(registry);
    }
}
