package Onlinestore.service;

import Onlinestore.entity.Item;
import Onlinestore.entity.User;
import Onlinestore.model.RoleNames;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DBInit(UserRepository userRepository, ItemRepository itemRepository, OrderRepository orderRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String[] args)
    {
        User user1 = new User("name1", "surname1", "email1@email.com",
                passwordEncoder.encode("bghnfghgnfghnbfgbh"), passwordEncoder.encode("password1"),
                "11111111111", "country1", "address111", new ArrayList<>(), RoleNames.ROLE_USER);
        User user2 = new User("name2", "surname2", "email2@email.com",
                passwordEncoder.encode("dydrveytbrbry"), passwordEncoder.encode("password2"),
                "22222222222", "country2", "address222", new ArrayList<>(), RoleNames.ROLE_ADMIN);
        userRepository.save(user1);
        userRepository.save(user2);
        
        Set<String> images1 = new HashSet<>();
        images1.add("image1.1");
        images1.add("image1.2");
        images1.add("image1.3");
        images1.add("image1.4");
        images1.add("image1.5");
        Map<String, String> specs1 = new HashMap<>();
        specs1.put("spec1", "value1");
        specs1.put("spec2", "value2");
        Item item1 = new Item("Item1", 25.5, 8, "description1", "logo1", images1, specs1);

        Set<String> images2 = new HashSet<>();
        images2.add("image2.1");
        images2.add("image2.2");
        images2.add("image2.3");
        images2.add("image2.4");
        images2.add("image2.5");
        Map<String, String> specs2 = new HashMap<>();
        specs2.put("spec1", "value1");
        specs2.put("spec2", "value2");
        Item item2 = new Item("Item2", 25.5, 8, "description2", "logo2", images2, specs2);

        Set<String> images3 = new HashSet<>();
        images3.add("image3.1");
        images3.add("image3.2");
        images3.add("image3.3");
        images3.add("image3.4");
        images3.add("image3.5");
        Map<String, String> specs3 = new HashMap<>();
        specs3.put("spec1", "value1");
        specs3.put("spec2", "value2");
        Item item3 = new Item("Item3", 25.5, 8, "description3", "logo3", images3, specs3);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }
}


// АДМИН
// админ не может покупать товары (ни смотреть корзину ни класть туда товары), но имеет полноценный профиль и может смотреть каталог

// АДМИН ПАНЕЛЬ
// при удалении товара админом удалять товар из всех существующих заказов
// проверить что б админ не установил товар в отрицательном количестве

// КАТАЛОГ
// не показывать товары у которых количество 0 штук

// ДОБАВЛЕНИЕ В КОРЗИНУ
// при добавлении в корзину товаров при оформлении заказа можно указать количество товара
// при добавлении товара в корзину проверять на клиенте что б количество товара в заказе было <= количества товаров в магазине и >= 1
// при добавлении товаров в корзину на сервере проверять что б количество товара в заказе было <= количества товаров в магазине и >= 1, иначе останавливать метод
// при оформлении корзины проверять на клиенте что б количество в заказе было <= количества товаров в магазине и >= 1, иначе на js заставлять клиента изменить количество товара
// при оформлении корзине проверять на сервере что б количество в заказе было <= количества товаров в магазине и >= 1, если нет то останавливать метод,
// если да то изменять товар в базе (если товар закончился то ставить количество 0) и если товар успешно списался из базы то списывать деньги, иначе останавливать метод