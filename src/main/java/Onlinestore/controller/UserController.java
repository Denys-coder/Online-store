package Onlinestore.controller;

import Onlinestore.dto.user.UpdateUserDTO;
import Onlinestore.dto.user.UserRegistrationDTO;
import Onlinestore.dto.user.UserResponseDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.user.UserRegistrationMapper;
import Onlinestore.mapper.user.UserResponseMapper;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserResponseMapper userResponseMapper;
    private final UserRepository userRepository;
    public final UserRegistrationMapper userRegistrationMapper;

    @GetMapping
    public ResponseEntity<?> getUser() {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserResponseDTO userResponseDTO = userResponseMapper.userToUserResponseDTO(user);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping
    public ResponseEntity<?> postUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {

        // Collect validation errors
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errors);
        }

        // Convert DTO to Entity and save
        User user = userRegistrationMapper.userRegistrationDtoToUserMapper(userRegistrationDTO);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Success"));
    }

}
