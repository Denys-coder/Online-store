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
        User userInDB = userRepository.getById(user.getId());
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
        orderRepository.save(order);
        user.addOrder(order);
        userInDB.addOrder(order);
        userRepository.save(userInDB);
        
        return "redirect:/catalog";
    }
    
    @PostMapping("/cart/delete-order")
    public String deleteOrder(@RequestParam("order_id") int orderId)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        User userInDB = userRepository.getById(user.getId());
    
        user.deleteOrder(orderId);
        userInDB.deleteOrder(orderId);
        
        userRepository.save(userInDB);
        orderRepository.deleteById(orderId);
        
        return "redirect:/cart";
    }
    
    @PostMapping("/cart/buy-orders")
    public String buyOrders(@RequestParam("amount_to_purchase") ArrayList<Integer> amountToPurchase)
    {
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        User userInDB = userRepository.getById(user.getId());
        List<Order> orders = userInDB.getOrders();
        List<Integer> ordersIds = new ArrayList<>();
        for (int i = 0; i < userInDB.getOrders().size(); i++)
        {
            ordersIds.add(orders.get(i).getId());
        }
        
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
        
        user.getOrders().clear();
        userInDB.getOrders().clear();
        
        userRepository.save(userInDB);
        for (int i = 0; i < ordersIds.size(); i++)
        {
            orderRepository.deleteById(ordersIds.get(i));
        }
        
        return "redirect:/cart";
    }
}
