package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.error.BadRequestDTO;
import Onlinestorerestapi.dto.order.*;
import Onlinestorerestapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "Get order by id",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Get your order. You need to be an the user who created specified order",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDTO.class)
            )
    )
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable int userId, @PathVariable int orderId) {

        OrderResponseDTO orderResponseDTO = orderService.getOrderResponseDTO(orderId, userId);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Get all your orders. You need to be user or admin to access it",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponseDTO.class)))
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all orders",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@PathVariable int userId) {

        List<OrderResponseDTO> orderResponseDTOs = orderService.getOrderResponseDTOs(userId);
        return ResponseEntity.ok(orderResponseDTOs);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Create order and receive newly created order. You need to be user or admin to access it",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDTO.class)
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Create order",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@PathVariable int userId, @Valid @RequestBody OrderCreateDTO orderCreateDTO) throws URISyntaxException {

        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderCreateDTO, userId);

        URI location = new URI("/api/v1/users/me/orders/" + orderResponseDTO.getId());
        return ResponseEntity
                .created(location)
                .body(orderResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Update order and receive newly updated order. You need to be the user who created it",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDTO.class)
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update order (need to specify all fields",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable int userId, @PathVariable int orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {

        OrderResponseDTO orderResponseDTO = orderService.updateOrder(orderId, orderUpdateDTO, userId);
        return ResponseEntity.ok().body(orderResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Patch order and receive newly patched order. You need to be the user who created it",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDTO.class)
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update order (need to specify only fields being updated",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> patchOrder(@PathVariable int userId, @PathVariable int orderId, @Valid @RequestBody OrderPatchDTO orderPatchDTO) {

        OrderResponseDTO orderResponseDTO = orderService.patchOrder(orderId, orderPatchDTO, userId);
        return ResponseEntity.ok().body(orderResponseDTO);
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Delete order",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @ApiResponse(responseCode = "200",
            description = "Delete order. You need to be the user who created it",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int userId, @PathVariable int orderId) {

        orderService.deleteOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Delete all orders. You need to be a user or an admin",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDTO.class)
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Delete all orders for current user",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @DeleteMapping
    public ResponseEntity<?> deleteOrders(@PathVariable int userId) {

        orderService.deleteOrders(userId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "400",
            description = "Returned when client request has error. Error description would be specified in body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDTO.class)
            )
    )
    @ApiResponse(responseCode = "403",
            description = "Returned when you have no authority to access this resource",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ApiResponse(responseCode = "200",
            description = "Fulfill orders and receive fulfilled order. You need to be a user or an admin",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderResponseDTO.class)
            )
    )
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Fulfill all orders for current user",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PostMapping("/fulfill")
    public ResponseEntity<List<OrderResponseDTO>> fulfillOrders(@PathVariable int userId) {

        List<OrderResponseDTO> orders = orderService.fulfillOrders(userId);
        return ResponseEntity.ok().body(orders);
    }
}
