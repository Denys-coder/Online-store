package Onlinestore.controller;

import Onlinestore.dto.user.UserLoginDTO;
import Onlinestore.dto.user.UserRegistrationDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.user.UserRegistrationMapper;
import Onlinestore.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    public final UserRegistrationMapper userRegistrationMapper;

    @PostMapping("/register")
    public ResponseEntity<?> checkAndRegisterUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {

        // Collect validation errors
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errors);
        }

        // Convert DTO to Entity and save
        User user = userRegistrationMapper.userRegistrationDtoToUserMapper(userRegistrationDTO);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Success"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {

        String username = userLoginDTO.getEmail();
        String password = userLoginDTO.getPassword();

        try {

            // load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // check if password is valid
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
            }

            // authenticate user
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // create session
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok(Map.of("message", "Success"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No such user"));
        }
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.ok("false");
    }
}
