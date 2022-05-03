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
import org.springframework.validation.BindingResult;
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
    public String getAddProductPage(Model model)
    {
        Item item = new Item();
        model.addAttribute("item", item);
        return "add-item";
    }
    
    @PostMapping("/admin/add-item")
    public String addItem(@ModelAttribute("item") @Valid Item item,
                             BindingResult bindingResult,
                             @RequestParam(value = "logo", required = false) MultipartFile logo,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             @RequestParam(value = "spec-names", required = false) ArrayList<String> specNames,
                             @RequestParam(value = "spec-values", required = false) ArrayList<String> specValues)
    {
        if (bindingResult.hasErrors())
        {
            return "add-item";
        }
        
        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount")))
        {
            return "redirect:/admin";
        }
        
        // fill item.specs
        if (specNames != null && specValues != null)
        {
            Map<String, String> specs = item.getSpecs();
            for (int i = 0; i < specNames.size(); i++)
            {
                specs.put(specNames.get(i), specValues.get(i));
            }
        }
        
        // save item to the database
        item = itemRepository.save(item);
        itemRepository.flush();
        int insertedId = item.getId();
        
        // (write logo image into file system) & (write logo name into item)
        if (logo != null && !logo.isEmpty())
        {
            String logoName = itemService.saveLogoToFolder(insertedId, logo);
            item.setLogoName(logoName);
        }
        else
        {
            item.setLogoName(null);
        }
        
        // (write images into file system) & (write image names into item)
        if (!images[0].isEmpty())
        {
            Set<String> imageNames = itemService.saveImagesToFolder(insertedId, images);
            item.setImageNames(imageNames);
        }
        else
        {
            item.setImageNames(null);
        }
        
        // write item into database
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
        model.addAttribute("item", item);
        
        return "update-item";
    }
    
    @PostMapping("/admin/update-item")
    public String updateItem(@ModelAttribute("item") @Valid Item item,
                             BindingResult bindingResult,
                             @RequestParam("logo") MultipartFile logo,
                             @RequestParam("images") MultipartFile[] images,
                             @RequestParam(value = "delete-previous-logo", required = false) boolean deletePreviousLogo,
                             @RequestParam(value = "delete-previous-images", required = false) boolean deletePreviousImages,
                             @RequestParam("spec-names") ArrayList<String> specNames,
                             @RequestParam("spec-values") ArrayList<String> specValues)
    {
        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images != null && images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount")))
        {
            return "error";
        }
        
        if (bindingResult.hasErrors())
        {
            return "update-item";
        }
        
        Item oldItem = itemRepository.getById(item.getId());
        String oldLogoName = oldItem.getLogoName();
        Set<String> oldImageNames = oldItem.getImageNames();
        item.setLogoName(oldLogoName);
        item.setImageNames(oldImageNames);
        itemRepository.save(item);
        
        // process logo
        if (deletePreviousLogo)
        {
            // delete from the database
            item.setLogoName(null);
            
            // delete from the folder
            itemService.deleteLogoFromFolder(item.getId());
        }
        else if (!logo.isEmpty())
        {
            // delete from the database
            item.setLogoName(null);
            
            // delete from the folder
            itemService.deleteLogoFromFolder(item.getId());
            
            // save file to the folder
            String logoName = itemService.saveLogoToFolder(item.getId(), logo);
            
            // save file to the database
            item.setLogoName(logoName);
        }
        
        if (deletePreviousImages)
        {
            // delete from the database
            item.setImageNames(null);
            
            // delete from the folder
            itemService.deleteImagesFromFolder(item.getId());
        }
        else if (!images[0].isEmpty())
        {
            // delete from the database
            item.setImageNames(null);
            
            // delete from the folder
            itemService.deleteImagesFromFolder(item.getId());
            
            // save files to the folder
            Set<String> imageNames = itemService.saveImagesToFolder(item.getId(), images);
            
            // save file to the database
            item.setImageNames(imageNames);
        }
        
        // fill item.specs
        Map<String, String> specs = new LinkedHashMap<>();
        for (int i = 0; i < specNames.size(); i++)
        {
            specs.put(specNames.get(i), specValues.get(i));
        }
        item.setSpecs(specs);
        itemRepository.save(item);
        
        return "redirect:/catalog";
    }
    
    @PostMapping("/admin/delete-item")
    public String deleteItem(@RequestParam("id") int itemId)
    {
        // delete user's orders from database
        List<User> users = userRepository.findAll();
        for (User user : users)
        {
            user.deleteOrdersByItemId(itemId);
            userRepository.save(user);
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
