package Onlinestore.service;

import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.model.RoleNames;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DBInit implements CommandLineRunner
{
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DBInit(UserRepository userRepository, ItemRepository itemRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String[] args)
    {
        User user1 = new User("name1", "surname1", "email1@email.com",
                passwordEncoder.encode("bghnfghgnfghnbfgbh"), passwordEncoder.encode("bghnfghgnfghnbfgbh"),
                "11111111111", "country1", "address111", new ArrayList<>(), RoleNames.ROLE_USER);
        User user2 = new User("name2", "surname2", "email2@email.com",
                passwordEncoder.encode("bghnfghgnfghnbfgbh"), passwordEncoder.encode("bghnfghgnfghnbfgbh"),
                "22222222222", "country2", "address222", new ArrayList<>(), RoleNames.ROLE_ADMIN);
        User user3 = new User("name3", "surname3", "email3@email.com",
                passwordEncoder.encode("fsdfgdfggdfgsf"), passwordEncoder.encode("fsdfgdfggdfgsf"),
                "33333333333", "country3", "address333", new ArrayList<>(), RoleNames.ROLE_USER);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        
        Set<String> images1 = new HashSet<>();
        images1.add("image1.1");
        images1.add("image1.2");
        images1.add("image1.3");
        images1.add("image1.4");
        Map<String, String> specs1 = new LinkedHashMap<>();
        specs1.put("Release date", "18th August 2021");
        specs1.put("RAM", "6GB");
        specs1.put("Screen type", "Super AMOLED");
        specs1.put("Refresh rate", "120Hz");
        specs1.put("Screen size (inches)", "6.43");
        specs1.put("Brightness", "1000 nits");
        specs1.put("Screen resolution", "1080x2400 pixels");
        specs1.put("Aspect ratio", "20:9");
        specs1.put("Processor", "Qualcomm Snapdragon 778G");
        specs1.put("Internal storage", "128GB");
        specs1.put("Battery capacity (mAh)", "4300");
        specs1.put("Charging", "Fast charging 65W, 100% in 33 min");
        specs1.put("Bluetooth", "5.2");
        specs1.put("NFC", "Yes");
        specs1.put("USB Type-C", "Yes");
        specs1.put("Headphones", "3.5mm");
        specs1.put("No. of Rear Cameras", "3");
        specs1.put("Rear autofocus", "Yes");
        specs1.put("Rear flash", "Yes");
        specs1.put("Rear camera", "64-megapixel (f/1.8, 0.7-micron) + 8-megapixel (f/2.3, 1.12-micron) + 2-megapixel (f/2.4, 88.8-micron)");
        specs1.put("Front camera", "32-megapixel (f/0.8)");
        specs1.put("No. of Front Cameras", "1");
        specs1.put("Operating system", "Android 11");
        specs1.put("Skin", "Realme UI 2.0");
        specs1.put("Dimensions (mm)", "159.20 x 73.50 x 8.00");
        specs1.put("Weight (g)", "186.00");
        specs1.put("Number of SIMs", "2");
        specs1.put("SIM Type", "Nano-SIM");
        specs1.put("5G", "Yes");
        specs1.put("In-Display Fingerprint Sensor", "Yes");
        specs1.put("Wi-Fi standards supported", "802.11 a/b/g/n/ac/ax");
        specs1.put("GPS", "Yes");
        specs1.put("Stereo Sound", "No");
        specs1.put("IP Protection", "No");
        specs1.put("Kit items", "smartphone, travel adapter, charging cable, SIM slot pin, protection case");
        Item item1 = new Item("Realme GT Master Edition 6/128GB Grey", 9999, 18, "Realme mobile phone", "logo1", images1, specs1);
        
        Set<String> images2 = new HashSet<>();
        images2.add("image2.1");
        images2.add("image2.2");
        images2.add("image2.3");
        images2.add("image2.4");
        images2.add("image2.5");
        Map<String, String> specs2 = new LinkedHashMap<>();
        specs2.put("Processor", "AMD Ryzen 5 5500U");
        specs2.put("GPU", "AMD Radeon Vega 7");
        specs2.put("RAM", "16GB");
        specs2.put("RAM frequency", "3200MGz");
        specs2.put("RAM type", "DDR4");
        specs2.put("Screen size (inches)", "15.6");
        specs2.put("Screen resolution", "1920x1080 (Full HD)");
        specs2.put("Screen coverage", "anti glare");
        specs2.put("Pixels density", "141ppi");
        specs2.put("Screen type", "IPS");
        specs2.put("Refresh rate", "60Hz");
        specs2.put("Empty RAM slots", "0");
        specs2.put("Data storage", "SSD disc");
        specs2.put("SSD size", "512GB");
        specs2.put("SSD type", "M.2 2242 PCIe 3.0x4 NVMe");
        specs2.put("Bluetooth", "5.1");
        specs2.put("Battery", "70Wh");
        specs2.put("Dimensions (mm)", "17.9-19.9 х 356.67 x 233.13 ");
        specs2.put("Color", "gray");
        specs2.put("Material", "plastic/aluminium");
        specs2.put("Weight (kg) ", "1.66");
        specs2.put("Kensington lock", "No");
        specs2.put("Video RAM", "uses RAM");
        specs2.put("HDMI", "1");
        specs2.put("LAN", "No");
        specs2.put("USB", "2 х 3.2 Gen 1");
        specs2.put("USB Type-C", "1 х 3.2 Gen 1");
        specs2.put("Headphones", "3.5mm");
        specs2.put("Card reader", "Yes");
        specs2.put("Card reader type", "SD");
        specs2.put("Web camera", "720p");
        specs2.put("Microphone", "Yes");
        specs2.put("Audio system", "Dolby Audio");
        specs2.put("Wi-Fi standards supported", "802.11 a/b/g/n/ac/ax");
        specs2.put("Kit items", "laptop, travel adapter");
        Item item2 = new Item("Lenovo IdeaPad 5 15ALC05 Graphite Grey", 24999, 8, "Lenovo laptop", "logo2", images2, specs2);
        
        Set<String> images3 = new HashSet<>();
        images3.add("image3.1");
        images3.add("image3.2");
        images3.add("image3.3");
        images3.add("image3.4");
        images3.add("image3.5");
        images3.add("image3.6");
        images3.add("image3.7");
        images3.add("image3.8");
        Map<String, String> specs3 = new LinkedHashMap<>();
        specs3.put("Color", "gray");
        specs3.put("Case weight (g)", "36.5");
        specs3.put("Earphone battery", "55mAh");
        specs3.put("Case battery", "215mAh");
        specs3.put("Bluetooth", "5.2");
        specs3.put("Earphones type", "TWS");
        specs3.put("Microphones", "4");
        specs3.put("Earphone dimensions (mm) height x width x depth", "37.5 x 21 x 23.9");
        specs3.put("Earphone weight (g)", "5.5 g");
        specs3.put("Case dimensions (mm) height x width x depth", "48 x 61.8 x 27.5");
        specs3.put("Active noise cancellation", "Yes");
        specs3.put("Call noise cancellation", "Yes");
        specs3.put("Kit items", "earbuds x2, charging case, silicone ear tips x3, USB-C charging cable");
        Item item3 = new Item("Huawei FreeBuds 4i Silver Frost", 1749, 12, "Huawei earphones", "logo3", images3, specs3);
        
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }
}