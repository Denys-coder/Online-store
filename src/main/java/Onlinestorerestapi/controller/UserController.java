package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "user", description = "Operations related to users")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user (which is logged in)")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {

        UserResponseDTO userResponseDTO = userService.getUserResponseDTO(userId);

        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Create new user")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        UserResponseDTO userResponseDTO = userService.createUser(userCreateDTO);

        URI location = URI.create("/api/v1/users/" + userResponseDTO.getId());
        return ResponseEntity
                .created(location)
                .body(userResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update current user (need to specify all fields")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO, @PathVariable int userId) {

        User user = userService.updateUser(userUpdateDTO, userId);

        return ResponseEntity.ok().body(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update current user (need to specify only fields being updated")
    @PatchMapping("/{userId}")
    public ResponseEntity<?> patchUser(@Valid @RequestBody UserPatchDTO userPatchDTO, @PathVariable int userId) {

        User user = userService.patchUser(userPatchDTO, userId);

        return ResponseEntity.ok().body(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Log out and delete current user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable int userId) {

        userService.deleteUser(request, response, userId);

        return ResponseEntity.noContent().build();
    }
}
