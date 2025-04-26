package Onlinestorerestapi.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUserDTO {

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String name;

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String surname;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

    @NotBlank
    @Size(min = 6, max = 12)
    @Pattern(regexp = "\\d*")
    private String telephoneNumber;

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String country;

    @NotBlank
    @Size(min = 10, max = 100)
    private String address;
}
