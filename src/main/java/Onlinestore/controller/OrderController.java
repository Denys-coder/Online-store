package Onlinestore.controller;

import Onlinestore.dto.order.GetOrderDTO;
import Onlinestore.dto.order.PostOrderDTO;
import Onlinestore.dto.order.PutOrderDTO;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.order.GetOrderMapper;
import Onlinestore.mapper.order.PutOrderMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.repository.OrderRepository;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/users/me/orders")
@AllArgsConstructor
public class OrderController {

    ItemRepository itemRepository;
    OrderRepository orderRepository;
    UserRepository userRepository;
    UserService userService;
    GetOrderMapper getOrderMapper;
    PutOrderMapper putOrderMapper;

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable int orderId) {

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        GetOrderDTO getOrderDTO = getOrderMapper.orderToGetOrderDTO(order);

        return ResponseEntity.ok(getOrderDTO);

    }

    @PostMapping
    public ResponseEntity<?> postOrder(@Valid @RequestBody PostOrderDTO postOrderDTO) throws URISyntaxException {

        int userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
        User user = userRepository.findById(userId).get();
        Item itemToOrder = itemRepository.getReferenceById(postOrderDTO.getItemId());

        Order newOrder = new Order(itemToOrder, postOrderDTO.getAmount(), user);
        orderRepository.save(newOrder);

        return ResponseEntity.created(new URI("/users/me/orders/" + newOrder.getId())).build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> putOrder(@PathVariable int orderId, @Valid @RequestBody PutOrderDTO putOrderDTO) {

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        putOrderMapper.mergePutOrderDTOIntoOrder(putOrderDTO, order);

        orderRepository.save(order);

        return ResponseEntity.ok("Order fields updated");

    }

}
