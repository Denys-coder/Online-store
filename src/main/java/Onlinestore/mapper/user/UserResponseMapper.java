package Onlinestore.mapper.user;

import Onlinestore.dto.user.UserResponseDTO;
import Onlinestore.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserResponseMapper {
    public abstract UserResponseDTO userToUserResponseDTO(User user);
}
