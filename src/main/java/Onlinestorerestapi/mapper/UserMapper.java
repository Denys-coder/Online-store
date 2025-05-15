package Onlinestorerestapi.mapper;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.RoleName;
import Onlinestorerestapi.entity.User;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Setter(onMethod_ = @Autowired)
    public PasswordEncoder passwordEncoder;
    public final RoleName userRole = RoleName.ROLE_USER;

    public abstract UserResponseDTO userToUserResponseDTO(User user);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userCreateDTO.getPassword()))")
    @Mapping(target = "roleName", expression = "java(userRole)")
    public abstract User userCreateDTOToUserMapper(UserCreateDTO userCreateDTO);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userUpdateDTO.getPassword()))")
    public abstract void mergeUserUpdateDTOIntoUser(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", expression = "java(userPatchDTO.getPassword() != null ? passwordEncoder.encode(userPatchDTO.getPassword()) : user.getPassword())")
    public abstract void mergeUserPatchDTOIntoUser(UserPatchDTO userPatchDTO, @MappingTarget User user);
}
