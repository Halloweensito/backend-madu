package com.Osiris.backendMadu.Exception;

import com.Osiris.backendMadu.DTO.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Recurso no encontrado (404)
    // Se dispara cuando lanzas EntityNotFoundException en tus servicios
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 2. Errores de Negocio / Lógica (400)
    // Se dispara con IllegalStateException o IllegalArgumentException (ej: color hex faltante, stock negativo)
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 3. Errores de Integridad de Datos (409)
    // Se dispara si intentas borrar un atributo que tiene productos, o duplicar un Unique Key (slug)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "Database error: Cannot delete or update because data is referenced elsewhere or exists.";

        // Puedes intentar parsear ex.getRootCause().getMessage() para ser más específico

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Data Integrity Violation",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 4. Errores de Validación de DTOs (@Valid) - (400)
    // Se dispara cuando falla un @NotBlank, @Size, @Pattern en el Controller
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        // Extraemos campo por campo qué falló
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Input data validation failed",
                request.getRequestURI()
        );
        errorResponse.setValidationErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}