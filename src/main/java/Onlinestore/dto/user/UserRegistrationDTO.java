package Onlinestore.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String telephoneNumber;
    private String country;
    private String address;
}
