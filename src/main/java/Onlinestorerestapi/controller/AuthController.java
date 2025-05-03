package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {

        userService.login(loginRequestDTO, request);

        return ResponseEntity.ok("User logged in");
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthentication() {

        AuthStatusDTO authStatusDTO = userService.checkAuthentication();

        return ResponseEntity.ok(authStatusDTO);
    }
}
