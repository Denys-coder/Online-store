package Onlinestore.service;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.model.RoleNames;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
        
        Item item1 = new Item("Item1", 123, 3, "description", "logo1", new HashSet<>(), new HashMap<>());
        itemRepository.save(item1);
        
        Order order1 = new Order(item1, 1);
        orderRepository.save(order1);
        
        user1.getOrders().add(order1);
        userRepository.save(user1);
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