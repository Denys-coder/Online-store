package Onlinestore.controller;

import Onlinestore.dto.order.*;
import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import Onlinestore.mapper.order.GetOrderMapper;
import Onlinestore.mapper.order.PatchOrderMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    PatchOrderMapper patchOrderMapper;

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

    @GetMapping
    public ResponseEntity<?> getOrders() {

        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUser(user);

        List<GetOrderDTO> getOrderDTOList = orders.stream()
                .map(getOrderMapper::orderToGetOrderDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(getOrderDTOList);

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

    @PatchMapping("/{orderId}")
    public ResponseEntity<?> putOrder(@PathVariable int orderId, @Valid @RequestBody PatchOrderDTO patchOrderDTO) {

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        patchOrderMapper.mergePatchOrderDTOIntoOrder(patchOrderDTO, order);

        orderRepository.save(order);

        return ResponseEntity.ok("Order fields updated");

    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.notFound().build();
        }

        orderRepository.delete(order);

        return ResponseEntity.noContent().build();
    }

}
