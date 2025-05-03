package Onlinestorerestapi.mapper.user;

import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserResponseMapper {
    public abstract UserResponseDTO userToUserResponseDTO(User user);
}
