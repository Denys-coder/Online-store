package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@Tag(name = "auth", description = "Operations related to authentication")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Logs into the system")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        UserResponseDTO userResponseDTO = authService.login(loginRequestDTO, request);

        Map<String, Object> response = Map.of("user", userResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check whether client is authenticated and its role")
    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus() {

        AuthStatusDTO authStatusDTO = authService.getAuthStatusDTO();

        return ResponseEntity.ok(authStatusDTO);
    }
}
