package Onlinestorerestapi.mapper.user;

import Onlinestorerestapi.dto.user.PutUserDTO;
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

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(putUserDTO.getPassword()))")
    public abstract void mergePutUserDTOIntoUser(PutUserDTO putUserDTO, @MappingTarget User user);
}
