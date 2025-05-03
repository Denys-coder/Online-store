package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserPatchDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.dto.user.UserUpdateDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import Onlinestorerestapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUser() {

        User user = userService.getCurrentUser();
        UserResponseDTO userResponseDTO = userMapper.userToUserResponseDTO(user);

        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping
    public ResponseEntity<?> postUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email address already in use");
        }

        if (userRepository.existsByTelephoneNumber(userCreateDTO.getTelephoneNumber())) {
            return ResponseEntity.badRequest().body("Telephone number already in use");
        }

        User user = userMapper.userCreateDTOToUserMapper(userCreateDTO);
        userRepository.save(user);

        return ResponseEntity.ok("User created");
    }

    @PutMapping("/me")
    public ResponseEntity<?> putUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {

        String email = userUpdateDTO.getEmail();
        if (userRepository.existsByEmail(email)
                && !email.equals(userService.getCurrentUser().getEmail())) {
            return ResponseEntity.badRequest().body("Email address should be unique or the same");
        }

        String telephoneNumber = userUpdateDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(userService.getCurrentUser().getTelephoneNumber())) {
            return ResponseEntity.badRequest().body("Telephone number should be unique or the same");
        }

        User currentUser = userService.getCurrentUser();
        userMapper.mergeUserUpdateDTOIntoUser(userUpdateDTO, currentUser);
        userRepository.save(currentUser);

        return ResponseEntity.ok("User updated");
    }

    @PatchMapping("/me")
    public ResponseEntity<?> patchUser(@Valid @RequestBody UserPatchDTO userPatchDTO) {

        String email = userPatchDTO.getEmail();
        if (email != null && userRepository.existsByEmail(email) && !email.equals(userService.getCurrentUser().getEmail())) {
            return ResponseEntity.badRequest().body("Email address should be unique or the same");
        }

        String telephoneNumber = userPatchDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(userService.getCurrentUser().getTelephoneNumber())) {
            return ResponseEntity.badRequest().body("Telephone number should be unique or the same");
        }

        User currentUser = userService.getCurrentUser();
        userMapper.mergeUserPatchDTOIntoUser(userPatchDTO, currentUser);
        userRepository.save(currentUser);

        return ResponseEntity.ok("User fields updated");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) {

        User user = userService.getCurrentUser();
        userRepository.delete(user);

        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.noContent().build();
    }
}
