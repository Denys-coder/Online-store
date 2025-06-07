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
    void getOrderResponseDTO_returnOrderResponseDTO() {
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
}
