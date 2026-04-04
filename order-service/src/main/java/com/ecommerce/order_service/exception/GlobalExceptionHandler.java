package com.ecommerce.order_service.exception;


import com.ecommerce.order_service.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){

        log.warn("Recurso no encontrado - Path: {}, message: {}", request.getDescription(false), ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                404,
                null,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Error de validación",
                HttpStatus.BAD_REQUEST.value(),
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex, WebRequest webRequest){

        log.warn("Ha ocurrido un error inesperado {}, message: {}", webRequest.getDescription(false), ex.getMessage(), ex);

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Ha ocurrido un error inesperado. Por favor, intente nuevamente más tarde o contacte a soporte.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HandleServiceConnectionFailure.class)
    public ResponseEntity<ApiResponse<Object>> handleServiceConnectionFailure(Exception ex, WebRequest webRequest){

        log.warn("Ha ocurrido un error de conexion {}, message: {}", webRequest.getDescription(false), ex.getMessage(), ex);

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Ha ocurrido un error de conexión con el servicio-inventory. Por favor, intente nuevamente más tarde o contacte a soporte.",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
