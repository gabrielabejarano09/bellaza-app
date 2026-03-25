package com.belleza_app.servicios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejo GLOBAL de excepciones para todo el módulo servicios-service.
 *
 * @RestControllerAdvice intercepta todas las excepciones lanzadas en los
 * controllers y las convierte en respuestas HTTP con formato JSON uniforme.
 *
 * Esto es lo que el profe llama "Control de excepciones no esperadas".
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 - Servicio no encontrado
     */
    @ExceptionHandler(ServicioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ServicioNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 422 - Violación de regla de negocio
     */
    @ExceptionHandler(ReglaNegocioServicioException.class)
    public ResponseEntity<Map<String, Object>> handleReglaNegocio(ReglaNegocioServicioException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /**
     * 400 - Validación de campos (@NotBlank, @Positive, @Min, etc.)
     * Se activa cuando el request body no pasa las validaciones de Jakarta Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // Recopila todos los errores de validación en un solo mensaje
        String errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce("", (a, b) -> a + "; " + b);

        return buildResponse(HttpStatus.BAD_REQUEST, "Errores de validación: " + errores);
    }

    /**
     * 500 - Cualquier excepción NO esperada que no tenga handler específico.
     * Esto evita que stacktraces internos se expongan al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor. Contacte al administrador.");
    }

    // ─── Helper ─────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}