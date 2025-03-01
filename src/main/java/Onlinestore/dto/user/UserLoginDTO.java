package Onlinestore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {

    @NotBlank
    @Size(min = 3, max = 20)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 32)
    private String password;
}
