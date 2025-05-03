package Onlinestorerestapi.dto.auth;

import Onlinestorerestapi.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthStatusDTO {
    private boolean authenticated;
    private RoleName roleName;
}
