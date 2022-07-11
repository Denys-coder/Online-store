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
import java.util.*;

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
        
        Set<Order> orders = user.getOrders();
        List<Order> orderList = new ArrayList<>(orders.stream().toList());
        orderList.sort(Comparator.comparing(Order::getId));
        
        model.addAttribute("orders", orders);
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));
        
        return "cart";
    }
    
    @PostMapping("/cart/add-order")
    public String addOrder(@RequestParam("item-id") int itemId)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Item item = itemRepository.getById(itemId);
        
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
        Set<Order> orders = user.getOrders();
        
        // check if amount to buy <= stored amount for each order
        Iterator<Order> orderIterator = orders.iterator();
        int currentAmountToPurchaseIndex = 0;
        while (orderIterator.hasNext())
        {
            if (amountToPurchase.get(currentAmountToPurchaseIndex) > orderIterator.next().getItem().getAmount())
            {
                return "redirect:/error";
            }
            currentAmountToPurchaseIndex++;
        }
        
        // update amount of stored items
        orderIterator = orders.iterator();
        currentAmountToPurchaseIndex = 0;
        while (orderIterator.hasNext())
        {
            Item item = orderIterator.next().getItem();
            item.setAmount(item.getAmount() - amountToPurchase.get(currentAmountToPurchaseIndex));
            currentAmountToPurchaseIndex++;
            itemRepository.save(item);
        }
        
        orderRepository.deleteAll(orders);
        
        user.getOrders().clear();
        userRepository.save(user);
        
        return "redirect:/cart";
    }
}
