package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.mapper.OrderMapper;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;

    public OrderResponseDTO getOrderResponseDTO(int orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "you have no such order");
        }

        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "you have no such order");
        }

        return orderMapper.orderToOrderResponseDTO(order);
    }
}
