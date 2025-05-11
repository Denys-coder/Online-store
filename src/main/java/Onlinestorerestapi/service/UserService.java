package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.RoleName;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import Onlinestorerestapi.security.UserPrincipal;
import Onlinestorerestapi.validation.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public void login(LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        String username = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", context);

        } catch (AuthenticationException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    public User getCurrentUser() {
        if (isAuthenticated()) {
            return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        }
        throw new IllegalStateException("User is not authenticated");
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
    }

    public AuthStatusDTO getAuthStatusDTO() {
        boolean authenticated = isAuthenticated();
        RoleName roleName = authenticated ? getCurrentUser().getRoleName() : null;
        return new AuthStatusDTO(authenticated, roleName);
    }

    public UserResponseDTO getUserResponseDTO() {
        User user = getCurrentUser();
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
                && !email.equals(getCurrentUser().getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address should be unique or the same");
        }

        String telephoneNumber = userUpdateDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(getCurrentUser().getTelephoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number should be unique or the same");
        }

        User currentUser = getCurrentUser();
        userMapper.mergeUserUpdateDTOIntoUser(userUpdateDTO, currentUser);
        userRepository.save(currentUser);
    }

    public void patchUser(UserPatchDTO userPatchDTO) {
        String email = userPatchDTO.getEmail();
        if (email != null && userRepository.existsByEmail(email) && !email.equals(getCurrentUser().getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email address should be unique or the same");
        }

        String telephoneNumber = userPatchDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(getCurrentUser().getTelephoneNumber())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Telephone number should be unique or the same");
        }

        User currentUser = getCurrentUser();
        userMapper.mergeUserPatchDTOIntoUser(userPatchDTO, currentUser);
        userRepository.save(currentUser);
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        User user = getCurrentUser();
        userRepository.delete(user);

        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }

}
