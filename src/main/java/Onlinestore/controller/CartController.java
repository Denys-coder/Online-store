package Onlinestore.controller;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController
{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    
    public CartController(ItemRepository itemRepository, UserRepository userRepository)
    {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/cart")
    public String getCartPage()
    {
        return "cart";
    }
    
    @PostMapping("/cart/add-order")
    public String addOrder(@RequestParam("item-id") int itemId)
    {
        Item item = itemRepository.getById(itemId);
        Order order = new Order(item, 1);
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        user.addOrder(order);
        userRepository.save(user);
        
        return "redirect:/catalog";
    }
}
