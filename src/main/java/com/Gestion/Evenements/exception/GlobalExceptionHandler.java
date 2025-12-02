package com.Gestion.Evenements.exception;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<Map<String, String>> handleStackOverflowError(StackOverflowError ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Infinite recursion detected");
        error.put("message", "Circular reference in entity relationships.");

        // Analyze stack trace to find recurring method/property
        String relationCause = detectCircularRelation(ex);
        error.put("cause", relationCause != null
                ? "Circular reference detected in: " + relationCause
                : "Likely caused by bidirectional relationship (check @JsonIgnore or @JsonBackReference)");

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String detectCircularRelation(StackOverflowError ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        Map<String, Integer> methodCount = new HashMap<>();

        for (StackTraceElement element : stackTrace) {
            String method = element.getClassName() + "." + element.getMethodName();
            methodCount.put(method, methodCount.getOrDefault(method, 0) + 1);

            // If a method repeats a lot, it's likely the cause of recursion
            if (methodCount.get(method) > 20) {
                return method; // returns something like "com.example.Domain.getProfileDomains"
            }
        }
        return null;
    }
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Map<String, String>> handleServletException(ServletException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());


        error.put("message", ex.getMessage() + "  " + ex.getCause().getMessage() + " \\ " + ex.getClass().getName() + " \\ " + ex.getStackTrace() );


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "The required files are missing from the request.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        StringBuilder errorMessageBuilder = new StringBuilder("Validation failed for fields: ");

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

            errorMessageBuilder.append(fieldName)
                    .append(" (")
                    .append(errorMessage)
                    .append("), ");
        });

        // Remove last comma and space if present
        String finalMessage = errorMessageBuilder.toString().replaceAll(", $", "");

        response.put("error", errors);              // field → message
        response.put("message", finalMessage);       // combined message
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handle malformed/missing JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid request body");
        error.put("message", ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Optional: handle all other uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }



    // ✅ Handle Custom Business Exception
    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<?> handleCustomAppException(CustomAppException ex) {
        return buildError(ex.getStatus(), ex.getTitle(), ex.getMessage());
    }


    // 🔁 Utility method to build response
    private ResponseEntity<Map<String, String>> buildError(HttpStatus status, String title, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", title);
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }



}