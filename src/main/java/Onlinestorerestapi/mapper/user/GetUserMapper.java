package Onlinestorerestapi.mapper.user;

import Onlinestorerestapi.dto.user.GetUserDTO;
import Onlinestorerestapi.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GetUserMapper {
    public abstract GetUserDTO userToGetUserDTO(User user);
}
