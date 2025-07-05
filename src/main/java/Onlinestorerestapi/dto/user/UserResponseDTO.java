package Onlinestorerestapi.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Integer id;

    private String name;

    private String surname;

    private String email;

    private String telephoneNumber;

    private String country;

    private String address;
}
