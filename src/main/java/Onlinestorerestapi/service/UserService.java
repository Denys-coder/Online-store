package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import Onlinestorerestapi.validation.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserResponseDTO getUserResponseDTO() {
        User user = authService.getCurrentUser();
        return userMapper.userToUserResponseDTO(user);
    }

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

    public void updateUser(UserUpdateDTO userUpdateDTO) {
        String email = userUpdateDTO.getEmail();
        if (userRepository.existsByEmail(email)
                && !email.equals(authService.getCurrentUser().getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address should be unique or the same");
        }

        String telephoneNumber = userUpdateDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(authService.getCurrentUser().getTelephoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number should be unique or the same");
        }

        User currentUser = authService.getCurrentUser();
        userMapper.mergeUserUpdateDTOIntoUser(userUpdateDTO, currentUser);
        userRepository.save(currentUser);
    }

    public void patchUser(UserPatchDTO userPatchDTO) {
        String email = userPatchDTO.getEmail();
        if (email != null && userRepository.existsByEmail(email) && !email.equals(authService.getCurrentUser().getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address should be unique or the same");
        }

        String telephoneNumber = userPatchDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(authService.getCurrentUser().getTelephoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number should be unique or the same");
        }

        User currentUser = authService.getCurrentUser();
        userMapper.mergeUserPatchDTOIntoUser(userPatchDTO, currentUser);
        userRepository.save(currentUser);
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        User user = authService.getCurrentUser();
        userRepository.delete(user);

        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }

}
