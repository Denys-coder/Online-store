package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {

        authService.login(loginRequestDTO, request);

        return ResponseEntity.ok("User logged in");
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus() {

        AuthStatusDTO authStatusDTO = authService.getAuthStatusDTO();

        return ResponseEntity.ok(authStatusDTO);
    }
}
