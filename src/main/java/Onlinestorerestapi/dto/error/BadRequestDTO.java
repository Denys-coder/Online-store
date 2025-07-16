package Onlinestorerestapi.dto.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

// 400 Bad Request
@Getter
@Setter
@NoArgsConstructor
public class BadRequestDTO {
    private String message; // general error message
    private Map<String, List<String>> errors; // field-specific validation errors

    public BadRequestDTO(String message, Map<String, List<String>> errors) {
        this.message = message;
        this.errors = errors;
    }
}
