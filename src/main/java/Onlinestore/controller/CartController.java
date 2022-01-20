package Onlinestore.controller;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class CartController
{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Environment environment;
    
    public CartController(ItemRepository itemRepository, UserRepository userRepository, Environment environment)
    {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.environment = environment;
    }
    
    @GetMapping("/cart")
    public String getCartPage(Model model)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<Order> orders = user.getOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));
        
        return "cart";
    }
    
    @PostMapping("/cart/add-order")
    public String addOrder(@RequestParam("item-id") int itemId)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Item item = itemRepository.getById(itemId);
    
        // check if user already bought it
        List<Order> userOrders = user.getOrders();
        for (Order order : userOrders)
        {
            if (order.getItem().getId() == item.getId())
            {
                return "redirect:/catalog";
            }
        }
        
        Order order = new Order(item, 1);
        user.addOrder(order);
        userRepository.save(user);
        
        return "redirect:/catalog";
    }
}
