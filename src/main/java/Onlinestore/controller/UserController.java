package Onlinestore.controller;

import Onlinestore.dto.user.UpdateUserDTO;
import Onlinestore.dto.user.UserRegistrationDTO;
import Onlinestore.dto.user.UserResponseDTO;
import Onlinestore.dto.user.GetUserDTO;
import Onlinestore.dto.user.PostUserDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.user.UserRegistrationMapper;
import Onlinestore.mapper.user.UserResponseMapper;
import Onlinestore.mapper.user.PostUserMapper;
import Onlinestore.mapper.user.GetUserMapper;
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

    private final GetUserMapper getUserMapper;
    private final UserRepository userRepository;
    public final PostUserMapper postUserMapper;

    @GetMapping
    public ResponseEntity<?> getUser() {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        GetUserDTO getUserDTO = getUserMapper.userToGetUserDTO(user);
        return ResponseEntity.ok(getUserDTO);
    }

    @PostMapping
    public ResponseEntity<?> postUser(@Valid @RequestBody PostUserDTO postUserDTO, BindingResult bindingResult) {

        // Collect validation errors
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errors);
        }

        // Convert DTO to Entity and save
        User user = postUserMapper.postUserDTOToUserMapper(postUserDTO);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Success"));
    }

}
