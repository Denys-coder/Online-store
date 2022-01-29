package Onlinestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController
{
    @GetMapping("/")
    public String getMainPage()
    {
        return "redirect:/about";
    }
    
    @GetMapping("/about")
    public String getAboutPage()
    {
        return "about";
    }
}
