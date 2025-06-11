package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.*;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import Onlinestorerestapi.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserResponseDTO getUserResponseDTO() {
        return userMapper.userToUserResponseDTO(authService.getCurrentUser());
    }

    @Transactional
    public void createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address already in use");
        }
        if (userRepository.existsByTelephoneNumber(userCreateDTO.getTelephoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number already in use");
        }
        User user = userMapper.userCreateDTOToUserMapper(userCreateDTO);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        User currentUser = authService.getCurrentUser();
        validateEmail(userUpdateDTO.getEmail(), currentUser.getEmail());
        validateTelephoneNumber(userUpdateDTO.getTelephoneNumber(), currentUser.getTelephoneNumber());

        userMapper.mergeUserUpdateDTOIntoUser(userUpdateDTO, currentUser);
        userRepository.save(currentUser);

        authService.refreshAuthenticatedUser(currentUser);
    }

    @Transactional
    public void patchUser(UserPatchDTO userPatchDTO) {
        User currentUser = authService.getCurrentUser();
        validateEmail(userPatchDTO.getEmail(), currentUser.getEmail());
        validateTelephoneNumber(userPatchDTO.getTelephoneNumber(), currentUser.getTelephoneNumber());

        userMapper.mergeUserPatchDTOIntoUser(userPatchDTO, currentUser);
        userRepository.save(currentUser);

        authService.refreshAuthenticatedUser(currentUser);
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        userRepository.delete(authService.getCurrentUser());
        new SecurityContextLogoutHandler().logout(
                request,
                response,
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    // ======= PRIVATE HELPERS =======

    private void validateEmail(String newEmail, String currentEmail) {
        if (newEmail != null && !newEmail.equals(currentEmail) && userRepository.existsByEmail(newEmail)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address should be unique or the same");
        }
    }

    private void validateTelephoneNumber(String newNumber, String currentNumber) {
        if (newNumber != null && !newNumber.equals(currentNumber) && userRepository.existsByTelephoneNumber(newNumber)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number should be unique or the same");
        }
    }
}
