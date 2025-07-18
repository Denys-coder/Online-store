package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.error.BadRequestDTO;
import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get current user (which is logged in)",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Get current user (yourself). You need to be authenticated",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable int userId) {

        UserResponseDTO userResponseDTO = userService.getUserResponseDTO(userId);

        return ResponseEntity.ok(userResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Create new user and receive newly created user. You need to be an admin",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
    )
    @Operation(
            summary = "Create new user",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        UserResponseDTO userResponseDTO = userService.createUser(userCreateDTO);

        URI location = URI.create("/api/v1/users/" + userResponseDTO.getId());
        return ResponseEntity
                .created(location)
                .body(userResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
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

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
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

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
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
