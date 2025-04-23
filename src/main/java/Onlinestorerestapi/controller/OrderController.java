package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.order.*;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.order.GetOrderMapper;
import Onlinestorerestapi.mapper.order.PatchOrderMapper;
import Onlinestorerestapi.mapper.order.PutOrderMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
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

        User user = userService.getCurrentUser();
        Item itemToOrder = itemRepository.getReferenceById(postOrderDTO.getItemId());

        // prevent adding order with the same item
        List<Order> userOrders = orderRepository.findByUser(user);
        List<Item> itemsInOrders = userOrders.stream()
                .map(Order::getItem)
                .toList();
        if (itemsInOrders.contains(itemToOrder)) {
            return ResponseEntity.badRequest().body("This item was already ordered");
        }

        // prevent adding order with order.amount > item.amount
        if (postOrderDTO.getAmount() > itemToOrder.getAmount()) {
            return ResponseEntity.badRequest().body("You try to order more than is available in stock");
        }

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
    public ResponseEntity<?> patchOrder(@PathVariable int orderId, @Valid @RequestBody PatchOrderDTO patchOrderDTO) {

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

    @DeleteMapping
    public ResponseEntity<?> deleteOrders() {

        orderRepository.deleteOrdersByUser(userService.getCurrentUser());
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/fulfill")
    public ResponseEntity<?> fulfillOrders() throws MethodArgumentNotValidException, NoSuchMethodException {

        // take all user's orders
        List<Order> orders = orderRepository.findByUser(userService.getCurrentUser());

        // check if order.amount <= item.amount, otherwise put error in BindingResult
        BindingResult bindingResult = new BeanPropertyBindingResult(orders, "order");
        for (Order order : orders) {
            if (order.getAmount() > order.getItem().getAmount()) {
                bindingResult.addError(new FieldError(
                        order.getItem().getName(),
                        "Order: " + order.getId(),
                        "Ordered amount (" + order.getAmount() + ") exceeds available stock ("
                                + order.getItem().getAmount() + ") for item: " + order.getItem().getName()
                ));
            }
            int itemAmount = order.getItem().getAmount();
            order.getItem().setAmount(itemAmount - order.getAmount());
        }

        if (bindingResult.hasErrors()) {
            Method method = this.getClass().getMethod("fulfillOrders");
            MethodParameter methodParameter = new MethodParameter(method, -1);
            throw new MethodArgumentNotValidException(methodParameter, bindingResult);
        }

        orderRepository.deleteAll(orders);
        itemRepository.saveAll(orders.stream().map(Order::getItem).toList());

        return ResponseEntity.noContent().build();

    }

}
