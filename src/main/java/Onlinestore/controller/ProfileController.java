package Onlinestore.controller;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
public class ProfileController
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping("/profile")
    public String getProfilePage(Model model)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        model.addAttribute(user);
        return "profile";
    }
    
    @GetMapping("/profile/change-profile-data")
    public String getChangeProfileDataPage(Model model)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        model.addAttribute(user);
        
        return "change-profile-data";
    }
    
    @PostMapping("/profile/change-profile-data")
    public String changeProfileData(@ModelAttribute("user") @Valid User user, BindingResult bindingResult)
    {
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        
        // check if new email already in use
        if (userRepository.existsByEmail(user.getEmail()) && !user.getEmail().equals(currentUser.getEmail()))
        {
            bindingResult.addError(new FieldError("user", "email", "email address already in use"));
        }
        
        // check if new telephoneNumber already in use
        if (userRepository.existsByTelephoneNumber(user.getTelephoneNumber()) && !user.getTelephoneNumber().equals(currentUser.getTelephoneNumber()))
        {
            bindingResult.addError(new FieldError("user", "telephoneNumber", "telephone number already in use"));
        }
        
        if (bindingResult.hasErrors())
        {
            return "change-profile-data";
        }
        
        currentUser.setName(user.getName());
        currentUser.setSurname(user.getSurname());
        currentUser.setEmail(user.getEmail());
        currentUser.setTelephoneNumber(user.getTelephoneNumber());
        currentUser.setCountry(user.getCountry());
        currentUser.setAddress(user.getAddress());
        userRepository.save(currentUser);
        
        return "redirect:/profile";
    }
    
    @GetMapping("/profile/change-password")
    public String getChangePasswordPage(Model model)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        model.addAttribute("user", user);
        
        return "change-password";
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@ModelAttribute("user") @Valid User userWithNewPassword, BindingResult bindingResult)
    {
        if (!userWithNewPassword.getPassword().equals(userWithNewPassword.getRepeatedPassword()))
        {
            bindingResult.addError(new FieldError("user", "repeatedPassword", "passwords doesn't match"));
        }
        
        if (bindingResult.hasErrors())
        {
            return "change-password";
        }
        else
        {
            User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            currentUser.setPassword(passwordEncoder.encode(userWithNewPassword.getPassword()));
            userRepository.save(currentUser);
    
            return "redirect:/profile";
        }
    }
    
    @GetMapping("/profile/delete-account")
    public String getDeleteAccountPage()
    {
        return "delete-account";
    }
    
    @PostMapping("/profile/delete-account")
    public String deleteAccount()
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userRepository.delete(user);
        
        return "redirect:/logout";
    }
}
