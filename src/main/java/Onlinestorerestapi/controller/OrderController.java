package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.order.*;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get order by id")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable int orderId) {

        OrderResponseDTO orderResponseDTO = orderService.getOrderResponseDTO(orderId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<?> getOrders() {

        List<OrderResponseDTO> orderResponseDTOs = orderService.getOrderResponseDTOs();
        return ResponseEntity.ok(orderResponseDTOs);
    }

    @Operation(summary = "Create order")
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) throws URISyntaxException {

        Order newOrder = orderService.createOrder(orderCreateDTO);
        return ResponseEntity.created(new URI("/users/me/orders/" + newOrder.getId())).build();
    }

    @Operation(summary = "Update order (need to specify all fields")
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable int orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {

        orderService.updateOrder(orderId, orderUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update order (need to specify only fields being updated")
    @PatchMapping("/{orderId}")
    public ResponseEntity<?> patchOrder(@PathVariable int orderId, @Valid @RequestBody OrderPatchDTO orderPatchDTO) {

        orderService.patchOrder(orderId, orderPatchDTO);
        return ResponseEntity.ok("Order fields updated");
    }

    @Operation(summary = "Delete order")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {

        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all orders for current user")
    @DeleteMapping
    public ResponseEntity<?> deleteOrders() {

        orderService.deleteOrders();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Fulfill all orders for current user")
    @PostMapping("/fulfill")
    public ResponseEntity<?> fulfillOrders() {

        orderService.fulfillOrders();
        return ResponseEntity.noContent().build();
    }
}
