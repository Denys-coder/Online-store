package Onlinestorerestapi.dto.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 401 Unauthorized
@Getter
@Setter
@NoArgsConstructor
public class UnauthorizedDTO {
    String message = "Invalid username or password";
}
