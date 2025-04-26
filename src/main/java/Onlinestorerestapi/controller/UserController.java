package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.user.GetUserDTO;
import Onlinestorerestapi.dto.user.PatchUserDTO;
import Onlinestorerestapi.dto.user.PostUserDTO;
import Onlinestorerestapi.dto.user.PutUserDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.user.PatchUserMapper;
import Onlinestorerestapi.mapper.user.PostUserMapper;
import Onlinestorerestapi.mapper.user.GetUserMapper;
import Onlinestorerestapi.mapper.user.PutUserMapper;
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

        if (userRepository.existsByEmail(postUserDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email address already in use");
        }

        if (userRepository.existsByTelephoneNumber(postUserDTO.getTelephoneNumber())) {
            return ResponseEntity.badRequest().body("Telephone number already in use");
        }

        User user = postUserMapper.postUserDTOToUserMapper(postUserDTO);
        userRepository.save(user);

        return ResponseEntity.ok("User created");
    }

    @PutMapping("/me")
    public ResponseEntity<?> putUser(@Valid @RequestBody PutUserDTO putUserDTO) {

        String email = putUserDTO.getEmail();
        if (email != null
                && userRepository.existsByEmail(email)
                && !email.equals(userService.getCurrentUser().getEmail())) {
            return ResponseEntity.badRequest().body("Email address should be unique or the same");
        }

        String telephoneNumber = putUserDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(userService.getCurrentUser().getTelephoneNumber())) {
            return ResponseEntity.badRequest().body("Telephone number should be unique or the same");
        }

        User currentUser = userService.getCurrentUser();
        putUserMapper.mergePutUserDTOIntoUser(putUserDTO, currentUser);
        userRepository.save(currentUser);

        return ResponseEntity.ok("User updated");
    }

    @PatchMapping("/me")
    public ResponseEntity<?> patchUser(@Valid @RequestBody PatchUserDTO patchUserDTO) {

        String email = patchUserDTO.getEmail();
        if (email != null && userRepository.existsByEmail(email) && !email.equals(userService.getCurrentUser().getEmail())) {
            return ResponseEntity.badRequest().body("Email address should be unique or the same");
        }

        String telephoneNumber = patchUserDTO.getTelephoneNumber();
        if (telephoneNumber != null
                && userRepository.existsByTelephoneNumber(telephoneNumber)
                && !telephoneNumber.equals(userService.getCurrentUser().getTelephoneNumber())) {
            return ResponseEntity.badRequest().body("Telephone number should be unique or the same");
        }

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
