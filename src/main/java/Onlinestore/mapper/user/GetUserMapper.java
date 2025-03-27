package Onlinestore.mapper.user;

import Onlinestore.dto.user.GetUserDTO;
import Onlinestore.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GetUserMapper {
    public abstract GetUserDTO userToGetUserDTO(User user);
}
