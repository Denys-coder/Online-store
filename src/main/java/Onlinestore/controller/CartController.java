package Onlinestore.controller;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController
{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final Environment environment;
    
    public CartController(ItemRepository itemRepository, UserRepository userRepository, OrderRepository orderRepository, Environment environment)
    {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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
        
        Order order = new Order(item, 1, user.getId());
        orderRepository.save(order);
        user.addOrder(order);
        userRepository.save(user);
        
        return "redirect:/catalog";
    }
    
    @PostMapping("/cart/delete-order")
    public String deleteOrder(@RequestParam("order_id") int orderId)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        
        user.deleteOrderById(orderId);
        
        userRepository.save(user);
        orderRepository.deleteById(orderId);
        
        return "redirect:/cart";
    }
    
    @PostMapping("/cart/buy-orders")
    public String buyOrders(@RequestParam("amount_to_purchase") ArrayList<Integer> amountToPurchase)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<Order> orders = user.getOrders();
        
        // check if amount to buy <= stored amount for each order
        for (int i = 0; i < orders.size(); i++)
        {
            if (amountToPurchase.get(i) > orders.get(i).getItem().getAmount())
            {
                return "redirect:/error";
            }
        }
        
        // update amount of stored items
        for (int i = 0; i < orders.size(); i++)
        {
            Item item = orders.get(i).getItem();
            item.setAmount(item.getAmount() - amountToPurchase.get(i));
            itemRepository.save(item);
        }
        
        orderRepository.deleteAll(orders);
        
        user.getOrders().clear();
        userRepository.save(user);
        
        return "redirect:/cart";
    }
}
