package ec.edu.ups.reservas.reportes.support;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, Object>> api(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(body(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Solicitud inválida");
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, message));
    }

    private Map<String, Object> body(HttpStatus status, String message) {
        return Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message);
    }
}
