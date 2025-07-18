package Onlinestorerestapi.exception;

import Onlinestorerestapi.dto.error.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handles spring.servlet.multipart.max-file-size and spring.servlet.multipart.max-request-size violation exceptions
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<PayloadTooLargeDTO> handleMaxUploadSizeException(MaxUploadSizeExceededException exception) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PayloadTooLargeDTO());
    }

    // missing RequestPart
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BadRequestDTO> handleMissingServletRequestPartException(MissingServletRequestPartException exception) {
        String errorMessage = String.format("Required part '%s' is missing", exception.getRequestPartName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BadRequestDTO(errorMessage, Collections.emptyMap()));
    }

    // Spring Boot fails to parse or map json
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequestDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BadRequestDTO("invalid json", Collections.emptyMap()));
    }

    // Spring Boot fails to parse or map query parameters or path variables
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BadRequestDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String parameterName = exception.getName();
        String expectedType = exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "Unknown";
        String invalidValue = exception.getValue() != null ? exception.getValue().toString() : "null";

        String errorMessage = String.format(
                "Parameter '%s' must be of type '%s'. Value '%s' is invalid.",
                parameterName, expectedType, invalidValue
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BadRequestDTO(errorMessage, Collections.emptyMap()));
    }

    // Spring Boot fails to validate @RequestBody fields
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestDTO> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BadRequestDTO("Body fields validation failed", errors));
    }

    // Spring Boot fails to validate @RequestParam, @PathVariable or direct method parameters
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BadRequestDTO> handleMethodValidationException(HandlerMethodValidationException exception) {
        Map<String, List<String>> errors = new HashMap<>();

        exception.getParameterValidationResults().forEach(result -> {
            String fieldName = Optional.ofNullable(result.getMethodParameter().getParameterName())
                    .orElse("unknown");

            result.getResolvableErrors().forEach(error -> {
                String message = error.getDefaultMessage();
                errors.computeIfAbsent(fieldName, key -> new ArrayList<>()).add(message);
            });
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BadRequestDTO("Validation failure", errors));
    }

    // when the request body has an unsupported format
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<UnsupportedMediaTypeDTO> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UnsupportedMediaTypeDTO(exception.getSupportedMediaTypes()));
    }

    // returned value could not be serialized into what the user needs
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<NotAcceptableDTO> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new NotAcceptableDTO("Supported media type is application/json"));
    }

    // returned value could be serialized into what the user needs, but failed
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<NotAcceptableDTO> handleHttpMessageNotWritableException(HttpMessageNotWritableException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new NotAcceptableDTO("Supported media type is application/json"));
    }

    // When a user is Unauthenticated
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<UnauthorizedDTO> handleBadCredentialsException(BadCredentialsException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UnauthorizedDTO());
    }

    // when BadRequestException is thrown
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestDTO> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BadRequestDTO(exception.getMessage(), exception.getErrors()));
    }

    // when NotFoundException is thrown
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<NotFoundDTO> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new NotFoundDTO(exception.getMessage()));
    }
}
