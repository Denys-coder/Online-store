package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
            summary = "Get current user (which is logged in)",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable int userId) {

        UserResponseDTO userResponseDTO = userService.getUserResponseDTO(userId);

        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(
            summary = "Create new user",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        UserResponseDTO userResponseDTO = userService.createUser(userCreateDTO);

        URI location = URI.create("/api/v1/users/" + userResponseDTO.getId());
        return ResponseEntity
                .created(location)
                .body(userResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update current user (need to specify all fields",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO, @PathVariable int userId) {
        UserResponseDTO user = userService.updateUser(userUpdateDTO, userId);

        return ResponseEntity.ok().body(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update current user (need to specify only fields being updated",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> patchUser(@Valid @RequestBody UserPatchDTO userPatchDTO, @PathVariable int userId) {

        UserResponseDTO userResponseDTO = userService.patchUser(userPatchDTO, userId);

        return ResponseEntity.ok().body(userResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Log out and delete current user",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable int userId) {

        userService.deleteUser(request, response, userId);

        return ResponseEntity.noContent().build();
    }
}
