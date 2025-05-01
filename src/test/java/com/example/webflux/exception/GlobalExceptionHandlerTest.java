package com.example.webflux.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        exceptionHandler = new GlobalExceptionHandler(objectMapper);
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
    }

    @Test
    void whenHandleTaskNotFoundExceptionShouldReturnNotFound() {
        TaskNotFoundException ex = new TaskNotFoundException("1");

        Mono<Void> result = exceptionHandler.handle(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.NOT_FOUND;
    }

    @Test
    void whenHandleTaskExceptionShouldReturnBadRequest() {
        TaskException ex = new TaskException(HttpStatus.BAD_REQUEST, "Bad request");

        Mono<Void> result = exceptionHandler.handle(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void whenHandleResponseStatusExceptionShouldReturnServerError() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");

        Mono<Void> result = exceptionHandler.handle(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Test
    void whenHandleGenericExceptionShouldReturnServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        Mono<Void> result = exceptionHandler.handle(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
