package com.example.webflux.exception;

import com.example.webflux.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        exceptionHandler = new GlobalExceptionHandler(objectMapper);
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
    }

    @Test
    void whenHandleTaskNotFoundExceptionShouldReturnNotFound() {
        TaskNotFoundException ex = new TaskNotFoundException("1");

        StepVerifier.create(exceptionHandler.handle(exchange, ex))
                .verifyComplete();

        ErrorResponse response = exchange.getAttribute("errorResponse");
        assert response != null;
        assert response.getStatus() == HttpStatus.NOT_FOUND.value();
        assert response.getMessage().contains("1");
    }

    @Test
    void whenHandleTaskExceptionShouldReturnBadRequest() {
        TaskException ex = new TaskException(HttpStatus.BAD_REQUEST, "Bad request");

        StepVerifier.create(exceptionHandler.handle(exchange, ex))
                .verifyComplete();

        ErrorResponse response = exchange.getAttribute("errorResponse");
        assert response != null;
        assert response.getStatus() == HttpStatus.BAD_REQUEST.value();
        assert response.getMessage().equals("Bad request");
    }

    @Test
    void whenHandleResponseStatusExceptionShouldReturnServerError() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");

        StepVerifier.create(exceptionHandler.handle(exchange, ex))
                .verifyComplete();

        ErrorResponse response = exchange.getAttribute("errorResponse");
        assert response != null;
        assert response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value();
        assert response.getMessage().contains("Server error");
    }

    @Test
    void whenHandleGenericExceptionShouldReturnServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        StepVerifier.create(exceptionHandler.handle(exchange, ex))
                .verifyComplete();

        ErrorResponse response = exchange.getAttribute("errorResponse");
        assert response != null;
        assert response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value();
        assert response.getMessage().contains("Unexpected error");
    }
}
