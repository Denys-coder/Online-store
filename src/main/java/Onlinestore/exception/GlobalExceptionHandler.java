package Onlinestore.exception;

import Onlinestore.validation.exception.item.MaxFileCountExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(error.getField(), key -> new java.util.ArrayList<>()).add(error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("One or more file exceed the maximum allowed size limit");
    }

    @ExceptionHandler(MaxFileCountExceededException.class)
    public ResponseEntity<String> handleMaxFileCountExceededException(MaxFileCountExceededException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, List<String>>> handleMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach(result -> {
            String fieldName = Optional.ofNullable(result.getMethodParameter().getParameterName())
                    .orElse("unknown");

            result.getResolvableErrors().forEach(error -> {
                String message = error.getDefaultMessage();
                errors.computeIfAbsent(fieldName, key -> new ArrayList<>()).add(message);
            });
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("invalid json");
    }
}
