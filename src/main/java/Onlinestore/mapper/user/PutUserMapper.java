package Onlinestore.mapper.user;

import Onlinestore.dto.user.PutUserDTO;
import Onlinestore.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class PutUserMapper {
    public abstract void mergePutUserDTOIntoUser(PutUserDTO patchUserDTO, @MappingTarget User user);
}