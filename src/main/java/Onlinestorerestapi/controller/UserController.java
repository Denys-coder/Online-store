package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUser() {

        UserResponseDTO userResponseDTO = userService.getUserResponseDTO();

        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping
    public ResponseEntity<?> postUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        userService.createUser(userCreateDTO);

        return ResponseEntity.ok("User created");
    }

    @PutMapping("/me")
    public ResponseEntity<?> putUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {

        userService.updateUser(userUpdateDTO);

        return ResponseEntity.ok("User updated");
    }

    @PatchMapping("/me")
    public ResponseEntity<?> patchUser(@Valid @RequestBody UserPatchDTO userPatchDTO) {

        userService.patchUser(userPatchDTO);

        return ResponseEntity.ok("User fields updated");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) {

        userService.deleteUser(request, response);

        return ResponseEntity.noContent().build();
    }
}
