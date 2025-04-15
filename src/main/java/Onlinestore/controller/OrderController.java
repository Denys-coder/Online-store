package Onlinestore.controller;

import Onlinestore.dto.order.PostOrderDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/users/me/orders")
@AllArgsConstructor
public class OrderController {

    ItemRepository itemRepository;
    OrderRepository orderRepository;
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> postOrder(@Valid @RequestBody PostOrderDTO postOrderDTO) throws URISyntaxException {

        int userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
        User user = userRepository.getById(userId);
        Item itemToOrder = itemRepository.getReferenceById(postOrderDTO.getItemId());

        Order newOrder = new Order(itemToOrder, postOrderDTO.getAmount(), user);
        orderRepository.save(newOrder);

        return ResponseEntity.created(new URI("/users/me/orders/" + newOrder.getId())).build();
    }

}
