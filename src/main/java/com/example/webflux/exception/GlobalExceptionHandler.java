package com.example.webflux.exception;

import com.example.webflux.model.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        
        // Determinar el estado HTTP y mensaje de error
        HttpStatus status = determineHttpStatus(ex);
        String message = determineMessage(ex);
        
        // Construir la respuesta de error
        ErrorResponse errorResponse = ErrorResponse.builder()
                .path(exchange.getRequest().getPath().value())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(LocalDateTime.now())
                .requestId(exchange.getRequest().getId())
                .build();

        // Configurar la respuesta
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Serializar y enviar la respuesta
        try {
            DataBuffer buffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
        } else if (ex instanceof TaskException) {
            return HttpStatus.valueOf(((TaskException) ex).getStatusCode().value());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineMessage(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException) ex).getReason();
        }
        return ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
    }
}
