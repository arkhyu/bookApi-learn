package org.ncp.bookapi.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for catching and formatting validation errors.
 *
 * This class is annotated with {@code @RestControllerAdvice}, which means it applies
 * globally to all controllers and allows centralized exception handling for REST APIs.
 *
 * Specifically, it handles {@link ConstraintViolationException}, which is thrown when
 * validation annotations on method parameters (like {@code @NotBlank} on {@code @RequestParam})
 * fail. For example, if a required query parameter is missing or empty.
 *
 * The {@code handleValidationException} method extracts the field names and messages from
 * the validation exception and returns a JSON response with:
 * {
 *   "fieldName": "Validation message"
 * }
 *
 * Response:
 * - HTTP Status: 400 Bad Request
 * - Body: Map of field → error message
 *
 * Notes for .NET developers:
 * - This works similarly to model validation and filters in ASP.NET Core's MVC pipeline.
 * - Think of this like a global filter that formats validation failures into client-friendly responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ConstraintViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            errorResponse.put(field, violation.getMessage());
        });

        return ResponseEntity.badRequest().body(errorResponse);
    }
}

