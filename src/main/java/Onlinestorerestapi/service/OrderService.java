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
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final ItemRepository itemRepository;
    private final OrderHelperService orderHelperService;

    public OrderResponseDTO getOrderResponseDTO(int orderId) {
        Order order = orderHelperService.getOrderForCurrentUserOrThrow(orderId);
        return orderMapper.orderToOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrderResponseDTOs() {
        User user = userService.getCurrentUser();
        return orderRepository.findByUser(user).stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Order createOrder(OrderCreateDTO orderCreateDTO) {
        User user = userService.getCurrentUser();
        Item item = orderHelperService.getItemOrThrow(orderCreateDTO.getItemId());

        boolean ordered = orderRepository.findByUser(user).stream()
                .map(Order::getItem)
                .anyMatch(i -> i.getId().equals(item.getId()));
        if (ordered) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This item was already ordered");
        }

        if (orderCreateDTO.getAmount() > item.getAmount()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You try to order more than is available in stock");
        }

        Order newOrder = orderMapper.orderCreateDTOToOrder(orderCreateDTO, item, user);
        return orderRepository.save(newOrder);
    }

    @Transactional
    public void updateOrder(int orderId, OrderUpdateDTO orderUpdateDTO) {
        if (orderId != orderUpdateDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Order id in the path and in the body should match");
        }

        orderHelperService.getItemOrThrow(orderUpdateDTO.getItemId());
        Order order = orderHelperService.getOrderForCurrentUserOrThrow(orderId);

        orderMapper.mergeOrderUpdateDTOIntoOrder(orderUpdateDTO, order);
        orderRepository.save(order);
    }

    @Transactional
    public void patchOrder(int orderId, OrderPatchDTO orderPatchDTO) {
        if (orderId != orderPatchDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Order id in the path and in the body should match");
        }

        if (orderPatchDTO.getItemId() != null) {
            orderHelperService.getItemOrThrow(orderPatchDTO.getItemId());
        }

        Order order = orderHelperService.getOrderForCurrentUserOrThrow(orderId);
        orderMapper.mergeOrderPatchDTOIntoOrder(orderPatchDTO, order);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(int orderId) {
        Order order = orderHelperService.getOrderForCurrentUserOrThrow(orderId);
        orderRepository.delete(order);
    }

    @Transactional
    public void deleteOrders() {
        orderRepository.deleteOrdersByUser(userService.getCurrentUser());
    }

    @Transactional
    public void fulfillOrders() {
        List<Order> orders = orderRepository.findByUser(userService.getCurrentUser());

        List<String> errors = new ArrayList<>();
        for (Order order : orders) {
            Item item = order.getItem();
            int availableAmount = item.getAmount();

            if (order.getAmount() > availableAmount) {
                errors.add(String.format(
                        "order: %s Ordered amount (%d) exceeds available stock (%d) for item: %s.",
                        order.getId(), order.getAmount(), availableAmount, item.getName()
                ));
            } else {
                item.setAmount(availableAmount - order.getAmount());
            }
        }

        if (!errors.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, errors);
        }

        orderRepository.deleteAll(orders);
        itemRepository.saveAll(orders.stream().map(Order::getItem).toList());
    }
}
