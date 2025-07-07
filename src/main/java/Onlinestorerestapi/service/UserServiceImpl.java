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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO getUserResponseDTO(int userId) {
        User currentUser = authService.getCurrentUser();

        if (currentUser.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        return userMapper.userToUserResponseDTO(currentUser);
    }

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address already in use");
        }
        if (userRepository.existsByTelephoneNumber(userCreateDTO.getTelephoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number already in use");
        }
        User user = userMapper.userCreateDTOToUserMapper(userCreateDTO);
        userRepository.save(user);

        return userMapper.userToUserResponseDTO(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public User updateUser(UserUpdateDTO userUpdateDTO, int userId) {

        User currentUser = authService.getCurrentUser();
        if (currentUser.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        validateEmail(userUpdateDTO.getEmail(), currentUser.getEmail());
        validateTelephoneNumber(userUpdateDTO.getTelephoneNumber(), currentUser.getTelephoneNumber());

        userMapper.mergeUserUpdateDTOIntoUser(userUpdateDTO, currentUser);
        userRepository.save(currentUser);

        authService.refreshAuthenticatedUser(currentUser);

        return currentUser;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void patchUser(UserPatchDTO userPatchDTO, int userId) {

        User currentUser = authService.getCurrentUser();
        if (currentUser.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        validateEmail(userPatchDTO.getEmail(), currentUser.getEmail());
        validateTelephoneNumber(userPatchDTO.getTelephoneNumber(), currentUser.getTelephoneNumber());

        userMapper.mergeUserPatchDTOIntoUser(userPatchDTO, currentUser);
        userRepository.save(currentUser);

        authService.refreshAuthenticatedUser(currentUser);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response, int userId) {

        User currentUser = authService.getCurrentUser();
        if (currentUser.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        userRepository.delete(currentUser);
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
