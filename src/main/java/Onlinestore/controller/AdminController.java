package Onlinestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController
{
    @GetMapping("/admin")
    public String getAdminPage()
    {
        return "admin";
    }
    
    @GetMapping("/admin/add-product")
    public String getAddProductPage()
    {
        return "add-product";
    }
    
    @PostMapping("/admin/add-product")
    public String addProduct()
    {
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/edit-or-delete-products")
    public String getEditOrDeletePage()
    {
        return "edit-or-delete-products";
    }
    
    @GetMapping("/admin/show-users")
    public String getShowUsersPage()
    {
        return "show-users";
    }
}
