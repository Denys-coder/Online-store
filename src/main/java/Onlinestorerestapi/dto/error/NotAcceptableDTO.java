package Onlinestorerestapi.dto.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 406 Not Acceptable
@Getter
@Setter
@NoArgsConstructor
public class NotAcceptableDTO {
    private String message;

    public NotAcceptableDTO(String message) {
        this.message = message;
    }
}
