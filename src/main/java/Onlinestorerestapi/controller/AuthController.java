package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.AuthenticatedDTO;
import Onlinestorerestapi.dto.user.UserLoginDTO;
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
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {

        userService.login(userLoginDTO, request);

        return ResponseEntity.ok("User logged in");
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthentication() {

        AuthenticatedDTO authenticatedDTO = userService.checkAuthentication();

        return ResponseEntity.ok(authenticatedDTO);
    }
}
