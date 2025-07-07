package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    UserResponseDTO getUserResponseDTO(int userId);

    UserResponseDTO createUser(UserCreateDTO userCreateDTO);

    User updateUser(UserUpdateDTO userUpdateDTO, int userId);

    User patchUser(UserPatchDTO userPatchDTO, int userId);

    void deleteUser(HttpServletRequest request, HttpServletResponse response, int userId);
}
