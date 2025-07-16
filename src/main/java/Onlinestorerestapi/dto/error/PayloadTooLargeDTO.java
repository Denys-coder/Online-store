package Onlinestorerestapi.dto.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 413 Payload Too Large
@Getter
@Setter
@NoArgsConstructor
public class PayloadTooLargeDTO {
    private String description = "One or more file exceed the maximum allowed size limit";

    public PayloadTooLargeDTO(String description) {
        this.description = description;
    }
}
