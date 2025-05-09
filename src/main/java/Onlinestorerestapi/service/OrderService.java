package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final ItemRepository itemRepository;

    @Transactional
    public OrderResponseDTO getOrderResponseDTO(int orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "You have no such order");
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "You have no such order");
        }

        return orderMapper.orderToOrderResponseDTO(order);
    }

    @Transactional
    public List<OrderResponseDTO> getOrderResponseDTOs() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Order createOrder(OrderCreateDTO orderCreateDTO) {

        // validate that postOrderDTO has existing item id
        if (!itemRepository.existsById(orderCreateDTO.getItemId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no item with the specified id");
        }

        User user = userService.getCurrentUser();
        Item itemToOrder = itemRepository.getReferenceById(orderCreateDTO.getItemId());

        // prevent adding order with the same item
        List<Order> userOrders = orderRepository.findByUser(user);
        List<Item> itemsInOrders = userOrders.stream()
                .map(Order::getItem)
                .toList();
        if (itemsInOrders.contains(itemToOrder)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This item was already ordered");
        }

        // prevent adding order with order.amount > item.amount
        if (orderCreateDTO.getAmount() > itemToOrder.getAmount()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You try to order more than is available in stock");
        }

        Order newOrder = orderMapper.orderCreateDTOToOrder(orderCreateDTO, itemToOrder, user);
        orderRepository.save(newOrder);

        return newOrder;
    }

    @Transactional
    public void updateOrder(int orderId, OrderUpdateDTO orderUpdateDTO) {
        if (orderId != orderUpdateDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Order id in the path and in the body should match");
        }

        // validate that postOrderDTO has existing item id
        if (!itemRepository.existsById(orderUpdateDTO.getItemId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no item with the specified id");
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "You have no such order");
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "You have no such order");
        }

        orderMapper.mergeOrderUpdateDTOIntoOrder(orderUpdateDTO, order);

        orderRepository.save(order);
    }
}
