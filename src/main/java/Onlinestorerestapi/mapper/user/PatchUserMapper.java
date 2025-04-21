package Onlinestorerestapi.mapper.user;

import Onlinestorerestapi.dto.user.PatchUserDTO;
import Onlinestorerestapi.entity.User;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class PatchUserMapper {

    @Setter(onMethod_ = @Autowired)
    public PasswordEncoder passwordEncoder;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(patchUserDTO.getPassword()))")
    public abstract void mergePatchUserDTOIntoUser(PatchUserDTO patchUserDTO, @MappingTarget User user);
}
