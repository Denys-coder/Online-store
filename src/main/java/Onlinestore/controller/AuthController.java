package Onlinestore.controller;

import Onlinestore.dto.user.UserLoginDTO;
import Onlinestore.dto.user.UserRegistrationDTO;
import Onlinestore.entity.User;
import Onlinestore.mapper.user.UserRegistrationMapper;
import Onlinestore.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    public final UserRegistrationMapper userRegistrationMapper;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserRegistrationMapper userRegistrationMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRegistrationMapper = userRegistrationMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> checkAndRegisterUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {

        User user = userRegistrationMapper.userRegistrationDtoToUserMapper(userRegistrationDTO);

        // check if email already in use
        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.addError(new FieldError("user", "email", "email address already in use"));
        }

        // check if telephoneNumber already in use
        if (userRepository.existsByTelephoneNumber(user.getTelephoneNumber())) {
            bindingResult.addError(new FieldError("user", "telephoneNumber", "telephone number already in use"));
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest httpServletRequest) {

        String username = userLoginDTO.getEmail();
        String password = userLoginDTO.getPassword();

        try {
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Verify password
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }
            User user = userRepository.findUserByEmail(userDetails.getUsername());

            // Authenticate user
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, password, List.of(new SimpleGrantedAuthority(user.getRoleName().toString())));
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create session
            HttpSession session = httpServletRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Generate response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", user.getName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<?> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.ok("false");
    }
}
