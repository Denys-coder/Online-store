package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    UserResponseDTO getUserResponseDTO();

    void createUser(UserCreateDTO userCreateDTO);

    void updateUser(UserUpdateDTO userUpdateDTO);

    void patchUser(UserPatchDTO userPatchDTO);

    void deleteUser(HttpServletRequest request, HttpServletResponse response);
}
