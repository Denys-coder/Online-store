package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.order.*;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Tag(name = "order", description = "Operations related to orders")
@RestController
@RequestMapping("/api/v1/users/{userId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get order by id")
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrder(@PathVariable int userId, @PathVariable int orderId) {

        OrderResponseDTO orderResponseDTO = orderService.getOrderResponseDTO(orderId, userId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<?> getOrders(@PathVariable int userId) {

        List<OrderResponseDTO> orderResponseDTOs = orderService.getOrderResponseDTOs(userId);
        return ResponseEntity.ok(orderResponseDTOs);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create order")
    @PostMapping
    public ResponseEntity<?> createOrder(@PathVariable int userId, @Valid @RequestBody OrderCreateDTO orderCreateDTO) throws URISyntaxException {

        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderCreateDTO, userId);

        URI location = new URI("/api/v1/users/me/orders/" + orderResponseDTO.getId());
        return ResponseEntity
                .created(location)
                .body(orderResponseDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update order (need to specify all fields")
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable int userId, @PathVariable int orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {

        Order order = orderService.updateOrder(orderId, orderUpdateDTO, userId);
        return ResponseEntity.ok().body(order);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update order (need to specify only fields being updated")
    @PatchMapping("/{orderId}")
    public ResponseEntity<?> patchOrder(@PathVariable int userId, @PathVariable int orderId, @Valid @RequestBody OrderPatchDTO orderPatchDTO) {

        Order order = orderService.patchOrder(orderId, orderPatchDTO, userId);
        return ResponseEntity.ok().body(order);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete order")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int userId, @PathVariable int orderId) {

        orderService.deleteOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete all orders for current user")
    @DeleteMapping
    public ResponseEntity<?> deleteOrders(@PathVariable int userId) {

        orderService.deleteOrders(userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Fulfill all orders for current user")
    @PostMapping("/fulfill")
    public ResponseEntity<?> fulfillOrders(@PathVariable int userId) {

        orderService.fulfillOrders(userId);
        return ResponseEntity.noContent().build();
    }
}
