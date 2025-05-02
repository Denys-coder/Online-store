package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.UserLoginDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.security.UserPrincipal;
import Onlinestorerestapi.validation.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;

    public void login(UserLoginDTO userLoginDTO, HttpServletRequest request) {
        String username = userLoginDTO.getEmail();
        String password = userLoginDTO.getPassword();

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

}
