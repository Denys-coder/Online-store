package Onlinestore.dto.user;

import Onlinestore.validation.annotation.UniqueOrSameOrNullEmail;
import Onlinestore.validation.annotation.UniqueOrSameOrNullTelephoneNumber;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUserDTO {

    @Nullable
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String name;

    @Nullable
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String surname;

    @Nullable
    @Size(max = 100)
    @Email
    @UniqueOrSameOrNullEmail
    private String email;

    @Nullable
    @Size(min = 8, max = 64)
    private String password;

    @Nullable
    @Size(min = 6, max = 12)
    @Pattern(regexp = "\\d*")
    @UniqueOrSameOrNullTelephoneNumber
    private String telephoneNumber;

    @Nullable
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^(?![- ])([a-zA-Z -]+)(?<![- ])$")
    private String country;

    @Nullable
    @Size(min = 10, max = 100)
    private String address;
}
