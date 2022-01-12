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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        User user = new User();
    
        user.setName(currentUser.getName());
        user.setSurname(currentUser.getSurname());
        user.setEmail(currentUser.getEmail());
        user.setTelephoneNumber(currentUser.getTelephoneNumber());
        user.setCountry(currentUser.getCountry());
        user.setAddress(currentUser.getAddress());
        
        model.addAttribute(user);
        
        return "change-profile-data";
    }
    
    @PostMapping("/profile/change-profile-data")
    public String changeProfileData(@Valid @ModelAttribute("user") User user, BindingResult bindingResult)
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
        
        if (bindingResult.hasFieldErrors("name")
        || bindingResult.hasFieldErrors("surname")
        || bindingResult.hasFieldErrors("email")
        || bindingResult.hasFieldErrors("telephoneNumber")
        || bindingResult.hasFieldErrors("country")
        || bindingResult.hasFieldErrors("address"))
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
    public String getChangePasswordPage()
    {
        return "change-password";
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("new_password") String newPassword,
                                 @RequestParam("repeated_new_password") String repeatedNewPassword)
    {
        if (newPassword.equals(repeatedNewPassword) && newPassword.length() >= 8 && newPassword.length() <= 64)
        {
            User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);
            return "redirect:/profile";
        }
        else
        {
            return "change-password";
        }
    }
    
    @GetMapping("/profile/delete-account")
    public String getDeleteAccountPage()
    {
        return "delete-page";
    }
    
    @PostMapping("/profile/delete-account")
    public String deleteAccount()
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userRepository.delete(user);
        return "redirect:/logout";
    }
}
