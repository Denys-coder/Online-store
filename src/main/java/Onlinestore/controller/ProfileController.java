package Onlinestore.controller;

import Onlinestore.entity.User;
import Onlinestore.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController
{
    @GetMapping("/profile")
    public String getProfilePage(Model model)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        model.addAttribute(user);
        return "profile";
    }
}
