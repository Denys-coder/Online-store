package Onlinestorerestapi.service;

import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public Item getItem(int itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item not found");
        }
        return itemOptional.get();
    }

    public boolean itemOrdered(Item item) {
        boolean ordered = false;
        if (userService.isAuthenticated()) {
            User user = userService.getCurrentUser();
            List<Order> orders = orderRepository.findByUser(user);

            // check if the user already bought it
            for (Order order : orders) {
                if (Objects.equals(order.getItem().getId(), item.getId())) {
                    ordered = true;
                    break;
                }
            }
        }
        return ordered;
    }

}
