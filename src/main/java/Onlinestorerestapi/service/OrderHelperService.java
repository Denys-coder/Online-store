package Onlinestorerestapi.service;

import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// this class is needed because @Transactional only works if the method is called from another class (not a case of self-invocation) and it is public
@Service
@AllArgsConstructor
public class OrderHelperService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Order getOrderForCurrentUserOrThrow(int orderId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userService.getCurrentUser().getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "You have no such order"));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Item getItemOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "There is no item with the specified id"));
    }
}
