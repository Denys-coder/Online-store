package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.dto.order.OrderUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.exception.BadRequestException;
import Onlinestorerestapi.exception.NotFoundException;
import Onlinestorerestapi.mapper.OrderMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        Order order = getOrderForCurrentUserOrThrow(orderId);
        return orderMapper.orderToOrderResponseDTO(order);
    }

    @PreAuthorize("isAuthenticated()")
    public List<OrderResponseDTO> getOrderResponseDTOs(int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
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
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        Item item = getItemOrThrow(orderCreateDTO.getItemId());

        boolean ordered = orderRepository.findByUser(user).stream()
                .map(Order::getItem)
                .anyMatch(currentItem -> currentItem.getId().equals(item.getId()));
        if (ordered) {
            throw new BadRequestException("This item was already ordered", Collections.emptyMap());
        }

        if (orderCreateDTO.getAmount() > item.getAmount()) {
            String exceptionMessage = String.format(
                    "You try to order (%d), but available stock is (%d).",
                    orderCreateDTO.getAmount(),
                    item.getAmount());
            throw new BadRequestException(exceptionMessage, Collections.emptyMap());
        }

        Order order = orderMapper.orderCreateDTOToOrder(orderCreateDTO, item, user);
        orderRepository.save(order);

        return orderMapper.orderToOrderResponseDTO(order);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public OrderResponseDTO updateOrder(int orderId, OrderUpdateDTO orderUpdateDTO, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        if (orderId != orderUpdateDTO.getId()) {
            throw new BadRequestException("Order id in the path and in the body should match", Collections.emptyMap());
        }

        // verify that item with specified id exists
        getItemOrThrow(orderUpdateDTO.getItemId());

        Order order = getOrderForCurrentUserOrThrow(orderId);

        orderMapper.mergeOrderUpdateDTOIntoOrder(orderUpdateDTO, order);
        orderRepository.save(order);

        return orderMapper.orderToOrderResponseDTO(order);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Order patchOrder(int orderId, OrderPatchDTO orderPatchDTO, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        if (orderId != orderPatchDTO.getId()) {
            throw new BadRequestException("Order id in the path and in the body should match", Collections.emptyMap());
        }

        // verify that item with specified id exists
        if (orderPatchDTO.getItemId() != null) {
            getItemOrThrow(orderPatchDTO.getItemId());
        }

        Order order = getOrderForCurrentUserOrThrow(orderId);
        orderMapper.mergeOrderPatchDTOIntoOrder(orderPatchDTO, order);
        orderRepository.save(order);

        return order;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteOrder(int orderId, int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        Order order = getOrderForCurrentUserOrThrow(orderId);
        orderRepository.delete(order);
    }

    public void deleteOrders(int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        orderRepository.deleteOrdersByUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public List<Order> fulfillOrders(int userId) {
        User user = authService.getCurrentUser();
        if (user.getId() != userId) {
            throw new BadRequestException("User id in path does not match with user id from session", Collections.emptyMap());
        }

        List<Order> orders = orderRepository.findByUser(authService.getCurrentUser());

        Map<String, List<String>> errors = new HashMap<>();
        for (Order order : orders) {
            Item item = order.getItem();
            int availableAmount = item.getAmount();

            if (order.getAmount() > availableAmount) {
                String key = String.format("order: %d", order.getId());
                String value = String.format("Ordered amount (%d) exceeds available stock (%d) for item: %s.", order.getAmount(), availableAmount, item.getName());
                errors.put(key, List.of(value));
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Some orders could not be fulfilled", errors);
        } else {
            for (Order order : orders) {
                Item item = order.getItem();
                int availableAmount = item.getAmount();
                item.setAmount(availableAmount - order.getAmount());
            }
        }

        orderRepository.deleteAll(orders);
        itemRepository.saveAll(orders.stream().map(Order::getItem).toList());

        return orders;
    }

    // ======= PRIVATE HELPERS =======

    private Order getOrderForCurrentUserOrThrow(int orderId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(authService.getCurrentUser().getId()))
                .orElseThrow(() -> new NotFoundException("You have no such order"));
    }

    private Item getItemOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("There is no item with the specified id"));
    }
}
