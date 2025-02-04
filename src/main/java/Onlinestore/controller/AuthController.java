package Onlinestore.controller;

import Onlinestore.entity.User;
import Onlinestore.model.RoleNames;
import Onlinestore.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> checkAndRegisterUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {

        // check if email already in use
        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "email address already in use"));
        }

        // check if telephoneNumber already in use
        if (userRepository.existsByTelephoneNumber(user.getTelephoneNumber())) {
            bindingResult.addError(new FieldError("user", "telephoneNumber", "telephone number already in use"));
        }
        
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.setRoleNames(RoleNames.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Verify password using BCrypt
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }

            User user = userRepository.findUserByEmail(userDetails.getUsername());
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password, List.of(new SimpleGrantedAuthority(user.getRoleNames().toString())))
            );

            // Generate response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", user.getName());
            response.put("roles", user.getRoleNames());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
