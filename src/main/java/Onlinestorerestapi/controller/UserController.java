package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user (which is logged in)")
    @GetMapping("/me")
    public ResponseEntity<?> getUser() {

        UserResponseDTO userResponseDTO = userService.getUserResponseDTO();

        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Create new user")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        userService.createUser(userCreateDTO);

        return ResponseEntity.ok("User created");
    }

    @Operation(summary = "Update current user (need to specify all fields")
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {

        userService.updateUser(userUpdateDTO);

        return ResponseEntity.ok("User updated");
    }

    @Operation(summary = "Update current user (need to specify only fields being updated")
    @PatchMapping("/me")
    public ResponseEntity<?> patchUser(@Valid @RequestBody UserPatchDTO userPatchDTO) {

        userService.patchUser(userPatchDTO);

        return ResponseEntity.ok("User fields updated");
    }

    @Operation(summary = "Log out and delete current user")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) {

        userService.deleteUser(request, response);

        return ResponseEntity.noContent().build();
    }
}
