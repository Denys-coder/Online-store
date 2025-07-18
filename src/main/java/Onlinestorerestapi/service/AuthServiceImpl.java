package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.entity.RoleName;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import Onlinestorerestapi.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        String username = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", context);

            Object principal = authentication.getPrincipal();
            String authenticatedUsername = ((UserDetails) principal).getUsername();
            User user = userRepository.findUserByEmail(authenticatedUsername);
            return userMapper.userToUserResponseDTO(user);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public User getCurrentUser() {
        if (isAuthenticated()) {
            int userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).user().getId();
            return userRepository.findById(userId).get();
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

    public void refreshAuthenticatedUser(User updatedUser) {

        UserDetails updatedUserDetails = new UserPrincipal(updatedUser);

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                currentAuth.getCredentials(),
                updatedUserDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
