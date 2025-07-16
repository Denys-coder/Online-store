package Onlinestorerestapi.dto.error;

import lombok.Getter;
import lombok.Setter;

// 404 Not Found
@Getter
@Setter
public class NotFoundDTO {
    private String message;

    public NotFoundDTO(String message) {
        this.message = message;
    }
}
