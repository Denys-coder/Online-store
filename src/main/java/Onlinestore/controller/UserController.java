package Onlinestore.controller;

import Onlinestore.dto.user.GetUserDTO;
import Onlinestore.dto.user.PatchUserDTO;
import Onlinestore.dto.user.PostUserDTO;
import Onlinestore.dto.user.PutUserDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.user.PatchUserMapper;
import Onlinestore.mapper.user.PostUserMapper;
import Onlinestore.mapper.user.GetUserMapper;
import Onlinestore.mapper.user.PutUserMapper;
import Onlinestore.repository.UserRepository;
import Onlinestore.service.UserService;
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

    private final GetUserMapper getUserMapper;
    private final UserRepository userRepository;
    private final PostUserMapper postUserMapper;
    private final PatchUserMapper patchUserMapper;
    private final PutUserMapper putUserMapper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUser() {

        User user = userService.getCurrentUser();
        GetUserDTO getUserDTO = getUserMapper.userToGetUserDTO(user);

        return ResponseEntity.ok(getUserDTO);
    }

    @PostMapping
    public ResponseEntity<?> postUser(@Valid @RequestBody PostUserDTO postUserDTO) {

        User user = postUserMapper.postUserDTOToUserMapper(postUserDTO);
        userRepository.save(user);

        return ResponseEntity.ok("User created");
    }

    @PutMapping("/me")
    public ResponseEntity<?> putUser(@Valid @RequestBody PutUserDTO putUserDTO) {

        User currentUser = userService.getCurrentUser();
        putUserMapper.mergePutUserDTOIntoUser(putUserDTO, currentUser);
        userRepository.save(currentUser);

        return ResponseEntity.ok("User updated");
    }

    @PatchMapping("/me")
    public ResponseEntity<?> patchUser(@Valid @RequestBody PatchUserDTO patchUserDTO) {

        User currentUser = userService.getCurrentUser();
        patchUserMapper.mergePatchUserDTOIntoUser(patchUserDTO, currentUser);
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
