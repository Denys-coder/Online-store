package Onlinestore.controller;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import Onlinestore.security.UserPrincipal;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Comparator;
import java.util.List;

@Controller
public class CatalogController
{
    private final ItemRepository itemRepository;
    private final Environment environment;
    
    public CatalogController(ItemRepository itemRepository, Environment environment)
    {
        this.itemRepository = itemRepository;
        this.environment = environment;
    }
    
    @GetMapping("/catalog")
    public String getCatalogPage(Model model)
    {
        List<Item> items = itemRepository.findAll();
        Comparator<Item> idComparator = Comparator.comparingInt(Item::getId);
        items.sort(idComparator);
        model.addAttribute("items", items);
        
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));
        
        return "catalog";
    }
    
    @GetMapping("/catalog/{id}")
    public String getShowItemPage(Model model, @PathVariable("id") int id)
    {
        Item item = itemRepository.findById(id).get();
        User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    
        // check if user already bought it
        boolean alreadyOrdered = false;
        List<Order> userOrders = user.getOrders();
        for (Order order : userOrders)
        {
            if (order.getItem().getId() == item.getId())
            {
                alreadyOrdered = true;
                break;
            }
        }
        model.addAttribute("alreadyOrdered", alreadyOrdered);
        
        model.addAttribute("item", item);
        model.addAttribute("logoFolder", environment.getProperty("item.logos.directory.on.server"));
        model.addAttribute("imagesFolder", environment.getProperty("item.images.directory.on.server"));
        
        return "item";
    }
}
