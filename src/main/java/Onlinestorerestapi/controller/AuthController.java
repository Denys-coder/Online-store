package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.auth.AuthStatusDTO;
import Onlinestorerestapi.dto.auth.LoginRequestDTO;
import Onlinestorerestapi.dto.error.BadRequestDTO;
import Onlinestorerestapi.dto.error.UnauthorizedDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Tag(name = "auth", description = "Operations related to authentication")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Logs into the system",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @ApiResponse(responseCode = "200",
            description = "Returned when user successfully logged in",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "401",
            description = "Username or password is invalid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UnauthorizedDTO.class)
            )
    )
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        UserResponseDTO userResponseDTO = authService.login(loginRequestDTO, request);

        return ResponseEntity.ok(userResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )@ApiResponse(responseCode = "200",
            description = "Show whether you authenticated and what is your role",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthStatusDTO.class)
            )
    )
    @Operation(summary = "Check whether client is authenticated and its role")
    @GetMapping("/status")
    public ResponseEntity<AuthStatusDTO> getAuthStatus() {

        AuthStatusDTO authStatusDTO = authService.getAuthStatusDTO();

        return ResponseEntity.ok(authStatusDTO);
    }
}
