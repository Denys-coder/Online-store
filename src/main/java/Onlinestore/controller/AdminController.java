package Onlinestore.controller;

import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminController
{
    private final ItemRepository itemRepository;
    private final Environment environment;
    
    public AdminController(ItemRepository itemRepository, Environment environment)
    {
        this.itemRepository = itemRepository;
        this.environment = environment;
    }
    
    @GetMapping("/admin")
    public String getAdminPage()
    {
        return "admin";
    }
    
    @GetMapping("/admin/add-product")
    public String getAddProductPage(@ModelAttribute("item") Item item)
    {
        return "add-product";
    }
    
    @PostMapping("/admin/add-product")
    public String addProduct(@Valid @ModelAttribute("item") Item item,
                             @RequestParam(value = "logo" , required = false) MultipartFile logo,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             @RequestParam(value = "spec-names", required = false) ArrayList<String> specNames,
                             @RequestParam(value = "spec-values", required = false) ArrayList<String> specValues)
    {
        // ensure that admin uploads no more that item.images.upload.max.amount files
        if (images == null || images.length > Integer.parseInt(environment.getProperty("item.images.upload.max.amount")))
        {
            return "redirect:/admin";
        }
        
        // if description is omitted set null
        if (item.getDescription() == null || item.getDescription().equals(""))
        {
            item.setDescription(null);
            itemRepository.save(item);
        }
        
        // fill item.specs
        if (specNames != null && specValues != null)
        {
            Map<String, String> specs = new HashMap<>();
            for (int i = 0; i < specNames.size(); i++)
            {
                specs.put(specNames.get(i), specValues.get(i));
            }
            item.setSpecs(specs);
        }
        
        // save item to database
        item = itemRepository.save(item);
        itemRepository.flush();
        
        // (write logo image into file system) & (write logo name into item) & (write item into database)
        if (logo != null && !logo.isEmpty())
        {
            int insertedId = item.getId();
            String logoName = "logo" + insertedId;
            
            String logosDirectory = environment.getProperty("item.logos.directory");
            File logoFile = new File(logosDirectory + logoName);
            try (OutputStream os = new FileOutputStream(logoFile))
            {
                os.write(logo.getBytes());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            item.setLogoName(logoName);
            itemRepository.save(item);
        }
        else
        {
            item.setLogoName(null);
            itemRepository.save(item);
        }
        
        // (write images into file system) & (write image names into item) & (write item into database)
        if (!images[0].isEmpty())
        {
            int insertedId = item.getId();
            String imageTemplateName = "image" + insertedId + ".";
            String itemsDirectory = environment.getProperty("item.images.directory");
            
            for (int i = 0; i < images.length; i++)
            {
                String localFileName = imageTemplateName + (i + 1);
                String fullImageName = itemsDirectory + localFileName;
                File imageFile = new File(fullImageName);
                try (OutputStream os = new FileOutputStream(imageFile))
                {
                    os.write(images[i].getBytes());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                
                item.addImageName(localFileName);
            }
            itemRepository.save(item);
        }
        else
        {
            item.setImageNames(null);
            itemRepository.save(item);
        }
        
        return "redirect:/admin";
    }
    
    @GetMapping("/admin/edit-or-delete-products")
    public String getEditOrDeletePage()
    {
        return "edit-or-delete-products";
    }
}
