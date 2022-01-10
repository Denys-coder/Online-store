package Onlinestore.controller;

import Onlinestore.entity.User;
import Onlinestore.model.RoleNames;
import Onlinestore.repository.UserRepository;
import Onlinestore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;

@Controller
public class AuthController
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
    
    @GetMapping("/login")
    public String getLoginPage()
    {
        return "login";
    }
    
    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("user") User userForm)
    {
        return "registration";
    }
    
    @PostMapping("/registration")
    public String checkAndRegisterUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult)
    {
        // check if email already in use
        if (!userService.isEmailUnique(user))
        {
            bindingResult.addError(new FieldError("user", "email", "email address already in use"));
        }
        
        // check if telephoneNumber already in use
        if (!userService.isTelephoneNumberUnique(user))
        {
            bindingResult.addError(new FieldError("user", "telephoneNumber", "telephone number already in use"));
        }
        
        // check if passwords match
        if (user.getRepeatedPassword() != null && !user.getPassword().equals(user.getRepeatedPassword()))
        {
            bindingResult.addError(new FieldError("user", "repeatedPassword", "passwords don't match"));
        }
        
        if (bindingResult.hasErrors())
        {
            return "registration";
        }
        
        user.setRoleNames(RoleNames.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        
        return "redirect:/login";
    }
}
