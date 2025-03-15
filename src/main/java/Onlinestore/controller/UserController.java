package Onlinestore.controller;

import Onlinestore.dto.user.UserResponseDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.user.UserResponseMapper;
import Onlinestore.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    public final UserResponseMapper userResponseMapper;

    @GetMapping
    public ResponseEntity<?> getUser() {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserResponseDTO userResponseDTO = userResponseMapper.userToUserResponseDTO(user);
        return ResponseEntity.ok(userResponseDTO);
    }

}
