package Onlinestorerestapi.validation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final List<String> errors;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errors = List.of(message);
    }

    public ApiException(HttpStatus status, List<String> errors) {
        super("Validation failed");
        this.status = status;
        this.errors = errors;
    }

}
