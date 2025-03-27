package Onlinestore.mapper.user;

import Onlinestore.dto.user.PostUserDTO;
import Onlinestore.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import Onlinestore.entity.RoleName;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Mapper(componentModel = "spring")
public abstract class PostUserMapper {

    @Setter(onMethod_ = @Autowired)
    public PasswordEncoder passwordEncoder;
    public final RoleName userRole = RoleName.ROLE_USER;

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(postUserDTO.getPassword()))")
    @Mapping(target = "roleName", expression = "java(userRole)")
    public abstract User postUserDTOToUserMapper(PostUserDTO postUserDTO);
}
