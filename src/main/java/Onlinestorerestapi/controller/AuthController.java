package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.UserLoginDTO;
import Onlinestorerestapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {

        String username = userLoginDTO.getEmail();
        String password = userLoginDTO.getPassword();

        userService.login(username, password, request);

        return ResponseEntity.ok("User logged in");
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthentication() {
        Map<String, Boolean> response = Collections.singletonMap("authenticated", userService.isAuthenticated());
        return ResponseEntity.ok(response);
    }
}
