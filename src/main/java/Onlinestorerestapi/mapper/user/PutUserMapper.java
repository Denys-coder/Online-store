package Onlinestorerestapi.mapper.user;

import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.User;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class PutUserMapper {

    @Setter(onMethod_ = @Autowired)
    public PasswordEncoder passwordEncoder;

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userUpdateDTO.getPassword()))")
    public abstract void mergeUserUpdateDTOIntoUser(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}
