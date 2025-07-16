package Onlinestorerestapi.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BadRequestException extends RuntimeException {
    private String message;
    private Map<String, List<String>> errors;

    public BadRequestException(String message, Map<String, List<String>> errors) {
        super(message);
        this.message = message;
        this.errors = errors;
    }
}
