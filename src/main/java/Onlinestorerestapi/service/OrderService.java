package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.OrderMapper;
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

    @Transactional
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

    @Transactional
    public List<OrderResponseDTO> getOrderResponseDTOs() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(orderMapper::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }
}
