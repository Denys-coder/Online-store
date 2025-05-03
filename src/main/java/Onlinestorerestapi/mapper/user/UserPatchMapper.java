package Onlinestorerestapi.mapper.user;

import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.entity.User;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserPatchMapper {

    @Setter(onMethod_ = @Autowired)
    public PasswordEncoder passwordEncoder;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userPatchDTO.getPassword()))")
    public abstract void mergeUserPatchDTOIntoUser(UserPatchDTO userPatchDTO, @MappingTarget User user);
}
