package Onlinestore.dto.user;

import Onlinestore.validation.annotation.user.UniqueOrSameEmail;
import Onlinestore.validation.annotation.user.UniqueOrSameTelephoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutUserDTO {
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
    @UniqueOrSameEmail
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

    @NotBlank
    @Size(min = 6, max = 12)
    @Pattern(regexp = "\\d*")
    @UniqueOrSameTelephoneNumber
    private String telephoneNumber;

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String country;

    @NotBlank
    @Size(min = 10, max = 100)
    private String address;
}
