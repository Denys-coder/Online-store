package Onlinestore.mapper.user;

import Onlinestore.dto.user.PatchUserDTO;
import Onlinestore.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class PatchUserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergePatchUserDTOIntoUser(PatchUserDTO patchUserDTO, @MappingTarget User user);
}