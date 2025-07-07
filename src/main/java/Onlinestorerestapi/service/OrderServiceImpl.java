package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.dto.order.OrderUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.OrderMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemRepository itemRepository;
    private final AuthService authService;

    @PreAuthorize("isAuthenticated()")
    public OrderResponseDTO getOrderResponseDTO(int orderId, int userId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        Order order = getOrderForCurrentUserOrThrow(orderId);
        return orderMapper.orderToOrderResponseDTO(order);
    }

    @PreAuthorize("isAuthenticated()")
    public List<OrderResponseDTO> getOrderResponseDTOs(int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        return orderRepository.findByUser(user).stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        Item item = getItemOrThrow(orderCreateDTO.getItemId());

        boolean ordered = orderRepository.findByUser(user).stream()
                .map(Order::getItem)
                .anyMatch(currentItem -> currentItem.getId().equals(item.getId()));
        if (ordered) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This item was already ordered");
        }

        if (orderCreateDTO.getAmount() > item.getAmount()) {
            String exceptionMessage = String.format(
                    "You try to order (%d), but available stock is (%d).",
                    orderCreateDTO.getAmount(),
                    item.getAmount());
            throw new ApiException(HttpStatus.BAD_REQUEST, exceptionMessage);
        }

        Order order = orderMapper.orderCreateDTOToOrder(orderCreateDTO, item, user);
        orderRepository.save(order);

        return orderMapper.orderToOrderResponseDTO(order);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Order updateOrder(int orderId, OrderUpdateDTO orderUpdateDTO, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        if (orderId != orderUpdateDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Order id in the path and in the body should match");
        }

        // verify that item with specified id exists
        getItemOrThrow(orderUpdateDTO.getItemId());

        Order order = getOrderForCurrentUserOrThrow(orderId);

        orderMapper.mergeOrderUpdateDTOIntoOrder(orderUpdateDTO, order);
        orderRepository.save(order);

        return order;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void patchOrder(int orderId, OrderPatchDTO orderPatchDTO, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        if (orderId != orderPatchDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Order id in the path and in the body should match");
        }

        // verify that item with specified id exists
        if (orderPatchDTO.getItemId() != null) {
            getItemOrThrow(orderPatchDTO.getItemId());
        }

        Order order = getOrderForCurrentUserOrThrow(orderId);
        orderMapper.mergeOrderPatchDTOIntoOrder(orderPatchDTO, order);
        orderRepository.save(order);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteOrder(int orderId, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        Order order = getOrderForCurrentUserOrThrow(orderId);
        orderRepository.delete(order);
    }

    public void deleteOrders(int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        orderRepository.deleteOrdersByUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void fulfillOrders(int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User id in path does not match with user id from session");
        }

        List<Order> orders = orderRepository.findByUser(authService.getCurrentUser());

        List<String> errors = new ArrayList<>();
        for (Order order : orders) {
            Item item = order.getItem();
            int availableAmount = item.getAmount();

            if (order.getAmount() > availableAmount) {
                errors.add(String.format(
                        "order: %d Ordered amount (%d) exceeds available stock (%d) for item: %s.",
                        order.getId(), order.getAmount(), availableAmount, item.getName()
                ));
            }
        }

        if (!errors.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, errors);
        } else {
            for (Order order : orders) {
                Item item = order.getItem();
                int availableAmount = item.getAmount();
                item.setAmount(availableAmount - order.getAmount());
            }
        }

        orderRepository.deleteAll(orders);
        itemRepository.saveAll(orders.stream().map(Order::getItem).toList());
    }

    // ======= PRIVATE HELPERS =======

    private Order getOrderForCurrentUserOrThrow(int orderId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(authService.getCurrentUser().getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "You have no such order"));
    }

    private Item getItemOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "There is no item with the specified id"));
    }
}
