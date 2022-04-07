package Onlinestore.controller;

import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.service.ItemService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.*;

@Controller
public class AdminController
{
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final Environment environment;
    private final ItemService itemService;
    
    public AdminController(ItemRepository itemRepository, OrderRepository orderRepository, UserRepository userRepository, Environment environment, ItemService itemService)
    {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.environment = environment;
        this.itemService = itemService;
    }
    
    @GetMapping("/admin")
    public String getAdminPage()
    {
        return "admin";
    }
    
    @GetMapping("/admin/add-item")
    public String getAddProductPage(@ModelAttribute("item") Item item)
    {
        return "add-item";
    }
    
    @PostMapping("/admin/add-item")
    public String addProduct(@Valid @ModelAttribute("item") Item item,
                             @RequestParam(value = "logo", required = false) MultipartFile logo,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             @RequestParam(value = "spec-names", required = false) ArrayList<String> specNames,
                             @RequestParam(value = "spec-values", required = false) ArrayList<String> specValues)
    {
        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount")))
        {
            return "redirect:/admin";
        }
        
        // if description is omitted set null
        if (item.getDescription() == null || item.getDescription().equals(""))
        {
            item.setDescription(null);
        }
        
        // fill item.specs
        if (specNames != null && specValues != null)
        {
            Map<String, String> specs = new LinkedHashMap<>();
            for (int i = 0; i < specNames.size(); i++)
            {
                specs.put(specNames.get(i), specValues.get(i));
            }
            item.setSpecs(specs);
        }
        
        // save item to the database
        item = itemRepository.save(item);
        itemRepository.flush();
        int insertedId = item.getId();
        
        // (write logo image into file system) & (write logo name into item) & (write item into database)
        if (logo != null && !logo.isEmpty())
        {
            String logoName = itemService.saveLogoToFolder(insertedId, logo);
            item.setLogoName(logoName);
        }
        else
        {
            item.setLogoName(null);
        }
        
        // (write images into file system) & (write image names into item) & (write item into database)
        if (!images[0].isEmpty())
        {
            Set<String> imageNames = itemService.saveImagesToFolder(insertedId, images);
            for (String imageName : imageNames)
            {
                item.addImageName(imageName);
            }
        }
        else
        {
            item.setImageNames(null);
        }
        itemRepository.save(item);
        
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/edit-or-delete-items")
    public String getEditOrDeletePage()
    {
        return "redirect:/catalog";
    }
    
    @GetMapping("/admin/update-item/{id}")
    public String getUpdateItemPage(@PathVariable("id") int id, Model model)
    {
        Item item = itemRepository.getById(id);
        
        model.addAttribute("id", id);
        model.addAttribute("name", item.getName());
        model.addAttribute("price", item.getPrice());
        model.addAttribute("amount", item.getAmount());
        model.addAttribute("description", item.getDescription());
        model.addAttribute("isLogoPresent", item.getLogoName() != null);
        model.addAttribute("isAnyImage", item.getImageNames() != null);
        model.addAttribute("specs", item.getSpecs());
        
        return "update-item";
    }
    
    @PostMapping("/admin/update-item")
    public String updateItem(@RequestParam("id") int id,
                             @RequestParam("name") String name,
                             @RequestParam("price") double price,
                             @RequestParam("amount") int amount,
                             @RequestParam("description") String description,
                             @RequestParam("logo") MultipartFile logo,
                             @RequestParam(value = "delete-previous-logo", required = false) boolean deletePreviousLogo,
                             @RequestParam("images") MultipartFile[] images,
                             @RequestParam(value = "delete-previous-images", required = false) boolean deletePreviousImages,
                             @RequestParam("spec-names") ArrayList<String> specNames,
                             @RequestParam("spec-values") ArrayList<String> specValues)
    {
        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images != null && images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount")))
        {
            return "redirect:/admin";
        }
        
        if (amount < 0)
        {
            return "redirect:/error";
        }
        
        Item currentItem = itemRepository.getById(id);
        currentItem.setName(name);
        currentItem.setPrice(price);
        currentItem.setAmount(amount);
        currentItem.setDescription(description);
        
        // process logo
        if (deletePreviousLogo)
        {
            // delete from the database
            currentItem.setLogoName(null);
            
            // delete from the folder
            itemService.deleteLogoFromFolder(id);
        }
        else if (!logo.isEmpty())
        {
            // delete from the database
            currentItem.setLogoName(null);
            
            // delete from the folder
            itemService.deleteLogoFromFolder(id);
            
            // save file to the folder
            String logoName = itemService.saveLogoToFolder(id, logo);
            
            // save file to the database
            currentItem.setLogoName(logoName);
        }
        
        if (deletePreviousImages)
        {
            // delete from the database
            currentItem.setImageNames(null);
            
            // delete from the folder
            itemService.deleteImagesFromFolder(id);
        }
        else if (!images[0].isEmpty())
        {
            // delete from the database
            currentItem.setImageNames(null);
            
            // delete from the folder
            itemService.deleteImagesFromFolder(id);
            
            // save files to the folder
            Set<String> imageNames = itemService.saveImagesToFolder(id, images);
            
            // save file to the database
            currentItem.setImageNames(imageNames);
        }
        
        // fill item.specs
        Map<String, String> specs = new LinkedHashMap<>();
        for (int i = 0; i < specNames.size(); i++)
        {
            specs.put(specNames.get(i), specValues.get(i));
        }
        currentItem.setSpecs(specs);
        itemRepository.save(currentItem);
        
        return "redirect:/catalog";
    }
    
    @PostMapping("/admin/delete-item")
    public String deleteItem(@RequestParam("id") int itemId)
    {
        // delete user's orders from database
        List<User> users = userRepository.findAll();
        for (int i = 0; i < users.size(); i++)
        {
            for (int j = 0; j < users.get(i).getOrders().size(); j++)
            {
                if (users.get(i).getOrders().get(j).getItem().getId() == itemId)
                {
                    users.get(i).getOrders().remove(users.get(i).getOrders().get(j));
                    break;
                }
            }
            userRepository.save(users.get(i));
        }
        
        // delete orders from database
        orderRepository.deleteOrdersByItem(itemRepository.getById(itemId));
    
        // delete item from database
        Item itemToDelete = itemRepository.getById(itemId);
        itemRepository.delete(itemToDelete);
        
        // delete logo and images from disc
        itemService.deleteLogoFromFolder(itemId);
        itemService.deleteImagesFromFolder(itemId);
        
        return "redirect:/catalog";
    }
}
