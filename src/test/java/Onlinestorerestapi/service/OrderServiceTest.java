package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.exception.ApiException;
import Onlinestorerestapi.mapper.OrderMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    ItemRepository itemRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    OrderService orderService;

    // should throw ApiException(HttpStatus.NOT_FOUND, "You have no such order"));
    @Test
    void getOrderResponseDTO_whenOrderNotExist_throwsApiException() {
        // given
        int orderId = 1;

        // when
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.getOrderResponseDTO(orderId));
        assertEquals("You have no such order", apiException.getMessage());
    }

    @Test
    void getOrderResponseDTO_whenValidRequest_returnOrderResponseDTO() {
        // given
        int orderId = 1;
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Order order = new Order();
        order.setUser(user);
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(orderId);

        // when
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // then
        assertEquals(orderId, orderService.getOrderResponseDTO(orderId).getId());
    }

    @Test
    void getOrderResponseDTOs_whenUserHasNotOrders_returnsEmptyList() {
        // given
        int userId = 1;
        User user = new User();
        user.setId(userId);
        List<OrderResponseDTO> orderResponseDTOs = new ArrayList<>();

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());

        // then
        assertEquals(orderResponseDTOs, orderService.getOrderResponseDTOs());
    }

    @Test
    void getOrderResponseDTOs_whenUserHasOrders_returnsOrderResponseDTOs() {
        // given
        int userId = 1;
        User user = new User();
        user.setId(userId);
        int orderId = 1;
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        int orderResponseDTOId = 1;
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(orderResponseDTOId);
        List<OrderResponseDTO> orderResponseDTOs = new ArrayList<>();
        orderResponseDTOs.add(orderResponseDTO);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // then
        assertEquals(orderResponseDTOs.get(0).getId(), orderService.getOrderResponseDTOs().get(0).getId());
    }
}
