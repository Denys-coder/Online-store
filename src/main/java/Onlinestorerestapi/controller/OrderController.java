package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.order.*;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/users/me/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable int orderId) {

        OrderResponseDTO orderResponseDTO = orderService.getOrderResponseDTO(orderId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @GetMapping
    public ResponseEntity<?> getOrders() {

        List<OrderResponseDTO> orderResponseDTOs = orderService.getOrderResponseDTOs();
        return ResponseEntity.ok(orderResponseDTOs);
    }

    @PostMapping
    public ResponseEntity<?> postOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) throws URISyntaxException {

        Order newOrder = orderService.createOrder(orderCreateDTO);
        return ResponseEntity.created(new URI("/users/me/orders/" + newOrder.getId())).build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> putOrder(@PathVariable int orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {

        orderService.updateOrder(orderId, orderUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<?> patchOrder(@PathVariable int orderId, @Valid @RequestBody OrderPatchDTO orderPatchDTO) {

        orderService.patchOrder(orderId, orderPatchDTO);
        return ResponseEntity.ok("Order fields updated");
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {

        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteOrders() {

        orderService.deleteOrders();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/fulfill")
    public ResponseEntity<?> fulfillOrders() {

        orderService.fulfillOrders();
        return ResponseEntity.noContent().build();
    }
}
