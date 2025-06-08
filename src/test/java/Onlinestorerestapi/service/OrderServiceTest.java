package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.entity.Item;
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

    @Test
    void createOrder_whenItemIdNonExisting_throwsApiException() {
        // given
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        int itemId = 1;
        orderCreateDTO.setItemId(itemId);
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO));
        assertEquals("There is no item with the specified id", apiException.getMessage());
    }

    @Test
    void createOrder_whenUserHasAlreadyOrderedItem_throwsApiException() {
        // given
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        User user = new User();
        int userId = 1;
        user.setId(userId);
        int itemID = 1;
        orderCreateDTO.setItemId(itemID);
        int orderId = 1;
        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        Item item = new Item();
        item.setId(itemID);
        order.setItem(item);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemID)).thenReturn(Optional.of(item));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO));
        assertEquals("This item was already ordered", apiException.getMessage());
    }

    @Test
    void createOrder_whenOrderAmountExceedsStock_throwsApiException() {
        // given
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setAmount(2);
        orderCreateDTO.setItemId(1);
        User user = new User();
        int userId = 1;
        user.setId(userId);
        Item itemToOrder = new Item();
        int itemToOrderId = 1;
        itemToOrder.setId(1);
        itemToOrder.setAmount(1);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemToOrderId)).thenReturn(Optional.of(itemToOrder));
        when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO));
        assertEquals("You try to order more than is available in stock", apiException.getMessage());
    }

    @Test
    void createOrder_whenValidRequest_createsOrder() {
        // given
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setAmount(1);
        orderCreateDTO.setItemId(1);
        User user = new User();
        int userId = 1;
        user.setId(userId);
        Item itemToOrder = new Item();
        int itemToOrderId = 1;
        itemToOrder.setId(1);
        itemToOrder.setAmount(1);
        Order newOrder = new Order();
        newOrder.setId(1);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemToOrderId)).thenReturn(Optional.of(itemToOrder));
        when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(orderMapper.orderCreateDTOToOrder(orderCreateDTO, itemToOrder, user)).thenReturn(newOrder);
        when(orderRepository.save(newOrder)).thenReturn(newOrder);

        // then
        assertEquals(newOrder, orderService.createOrder(orderCreateDTO));
    }
}
