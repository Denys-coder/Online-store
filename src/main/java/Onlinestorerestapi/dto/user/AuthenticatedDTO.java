package Onlinestorerestapi.dto.user;

import Onlinestorerestapi.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthenticatedDTO {
    private boolean authenticated;
    private RoleName roleName;
}
