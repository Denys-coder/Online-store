package Onlinestore.mapper.user;

import Onlinestore.dto.user.UserRegistrationDTO;
import Onlinestore.entity.User;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import Onlinestore.model.RoleName;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class UserRegistrationMapper {

    public PasswordEncoder passwordEncoder;

    public RoleName userRole = RoleName.ROLE_USER;

    protected UserRegistrationMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userRegistrationDTO.getPassword()))")
    @Mapping(target = "roleName", expression = "java(userRole)")
    public abstract User userRegistrationDtoToUserMapper(UserRegistrationDTO userRegistrationDTO);
}
