package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    UserResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletRequest request);

    User getCurrentUser();

    boolean isAuthenticated();

    AuthStatusDTO getAuthStatusDTO();

    void refreshAuthenticatedUser(User updatedUser);
}
