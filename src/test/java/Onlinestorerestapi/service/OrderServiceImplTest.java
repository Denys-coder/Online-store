package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.dto.order.OrderUpdateDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    ItemRepository itemRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void getOrderResponseDTO_whenUserIdsMismatch_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        int orderId = 1;

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.getOrderResponseDTO(orderId, 2));
        assertEquals("User id in path does not match with user id from session", apiException.getMessage());
    }

    @Test
    void getOrderResponseDTO_whenOrderNotExist_throwsApiException() {
        // given
        int orderId = 1;
        int userId = 1;
        User user = new User();
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.getOrderResponseDTO(orderId, userId));
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
        assertEquals(orderId, orderService.getOrderResponseDTO(orderId, userId).getId());
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
        assertEquals(orderResponseDTOs, orderService.getOrderResponseDTOs(userId));
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
        assertEquals(orderResponseDTOs.get(0).getId(), orderService.getOrderResponseDTOs(userId).get(0).getId());
    }

    @Test
    void createOrder_whenUserIdsMismatch_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO, 2));
        assertEquals("User id in path does not match with user id from session", apiException.getMessage());
    }

    @Test
    void createOrder_whenItemIdNotExists_throwsApiException() {
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
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO, userId));
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
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO, userId));
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
        String exceptionMessage = String.format(
                "You try to order (%d), but available stock is (%d).",
                orderCreateDTO.getAmount(),
                itemToOrder.getAmount());
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.createOrder(orderCreateDTO, userId));
        assertEquals(exceptionMessage, apiException.getMessage());
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
        int orderId = 1;
        Order order = new Order();
        order.setId(orderId);
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(orderId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemToOrderId)).thenReturn(Optional.of(itemToOrder));
        when(orderRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(orderMapper.orderCreateDTOToOrder(orderCreateDTO, itemToOrder, user)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.orderToOrderResponseDTO(order)).thenReturn(orderResponseDTO);

        // then
        assertEquals(orderResponseDTO, orderService.createOrder(orderCreateDTO, userId));
    }

    @Test
    void updateOrder_whenUserIdsMismatch_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO();
        int orderId = 1;

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.updateOrder(orderId, orderUpdateDTO, 2));
        assertEquals("User id in path does not match with user id from session", apiException.getMessage());
    }

    @Test
    void updateOrder_whenOrderIdsMismatch_throwsApiException() {
        // given
        int orderId = 1;
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO();
        orderUpdateDTO.setId(2);
        User user = new User();
        int userID = 1;
        user.setId(userID);

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.updateOrder(orderId, orderUpdateDTO, userID));
        assertEquals("Order id in the path and in the body should match", apiException.getMessage());
    }

    @Test
    void updateOrder_whenItemIdNotExists_throwsApiException() {
        // given
        int orderId = 1;
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO();
        orderUpdateDTO.setId(orderId);
        int itemId = 1;
        orderUpdateDTO.setItemId(itemId);
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.updateOrder(orderId, orderUpdateDTO, userId));
        assertEquals("There is no item with the specified id", apiException.getMessage());
    }

    @Test
    void updateOrder_whenUserHasNoSuchOrder_throwsApiException() {
        // given
        int orderId = 1;
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO();
        orderUpdateDTO.setId(orderId);
        int itemId = 1;
        orderUpdateDTO.setItemId(itemId);
        Item item = new Item();
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.updateOrder(orderId, orderUpdateDTO, userId));
        assertEquals("You have no such order", apiException.getMessage());
    }

    @Test
    void updateOrder_whenValidaRequest_updatesOrder() {
        // given
        int orderId = 1;
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO();
        orderUpdateDTO.setId(orderId);
        int itemId = 1;
        orderUpdateDTO.setItemId(itemId);
        Item item = new Item();
        Order order = new Order();
        User user = new User();
        int userId = 1;
        user.setId(userId);
        order.setUser(user);

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        Order updatedOrder = orderService.updateOrder(orderId, orderUpdateDTO, userId);
        assertEquals(order, updatedOrder);
        verify(orderMapper).mergeOrderUpdateDTOIntoOrder(orderUpdateDTO, order);
        verify(orderRepository).save(order);
    }

    @Test
    void patchOrder_whenUserIdsMismatch_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        OrderPatchDTO orderPatchDTO = new OrderPatchDTO();
        int orderId = 1;

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.patchOrder(orderId, orderPatchDTO, 2));
        assertEquals("User id in path does not match with user id from session", apiException.getMessage());
    }

    @Test
    void patchOrder_whenOrderIdsMismatch_throwsApiException() {
        // given
        int orderId = 1;
        OrderPatchDTO orderPatchDTO = new OrderPatchDTO();
        orderPatchDTO.setId(2);
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.patchOrder(orderId, orderPatchDTO, userId));
        assertEquals("Order id in the path and in the body should match", apiException.getMessage());
    }

    @Test
    void patchOrder_whenItemIdNotExists_throwsApiException() {
        // given
        int orderId = 1;
        OrderPatchDTO orderPatchDTO = new OrderPatchDTO();
        orderPatchDTO.setId(orderId);
        int itemId = 1;
        orderPatchDTO.setItemId(itemId);
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.patchOrder(orderId, orderPatchDTO, userId));
        assertEquals("There is no item with the specified id", apiException.getMessage());
    }

    @Test
    void patchOrder_whenUserHasNoSuchOrder_throwsApiException() {
        // given
        int orderId = 1;
        OrderPatchDTO orderPatchDTO = new OrderPatchDTO();
        orderPatchDTO.setId(orderId);
        int itemId = 1;
        orderPatchDTO.setItemId(itemId);
        Item item = new Item();
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.patchOrder(orderId, orderPatchDTO, userId));
        assertEquals("You have no such order", apiException.getMessage());
    }

    @Test
    void patchOrder_whenValidaRequest_updatesOrder() {
        // given
        int orderId = 1;
        OrderPatchDTO orderPatchDTO = new OrderPatchDTO();
        orderPatchDTO.setId(orderId);
        int itemId = 1;
        orderPatchDTO.setItemId(itemId);
        Item item = new Item();
        Order order = new Order();
        User user = new User();
        int userId = 1;
        user.setId(userId);
        order.setUser(user);

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        Order patchedOrder = orderService.patchOrder(orderId, orderPatchDTO, userId);
        assertEquals(order, patchedOrder);
        verify(orderMapper).mergeOrderPatchDTOIntoOrder(orderPatchDTO, order);
        verify(orderRepository).save(order);
    }

    @Test
    void deleteOrder_whenUserIdsMismatch_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        int orderId = 1;

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.deleteOrder(orderId, 2));
        assertEquals("User id in path does not match with user id from session", apiException.getMessage());
    }

    @Test
    void deleteOrder_whenUserHasNoSuchOrder_throwsApiException() {
        // given
        int orderId = 1;
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.deleteOrder(orderId, userId));
        assertEquals("You have no such order", apiException.getMessage());
    }

    @Test
    void deleteOrder_whenValidRequest_deletesOrder() {
        // given
        int orderId = 1;
        Order order = new Order();
        User user = new User();
        int userId = 1;
        user.setId(userId);
        order.setUser(user);

        // when
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(authService.getCurrentUser()).thenReturn(user);
        doNothing().when(orderRepository).delete(order);

        // then
        orderService.deleteOrder(orderId, userId);
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrders_whenCalled_deletesOrders() {
        // given
        int userId = 1;
        User user = new User();
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        orderService.deleteOrders(userId);
        verify(orderRepository).deleteOrdersByUser(user);
    }

    @Test
    void fulfillOrders_whenUserIdsMismatch_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(authService.getCurrentUser()).thenReturn(user);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.fulfillOrders(2));
        assertEquals("User id in path does not match with user id from session", apiException.getMessage());
    }

    @Test
    void fulfillOrders_whenAllOrdersFulfilled_fulfillOrders() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        Order order2 = new Order();
        order1.setAmount(1);
        order2.setAmount(1);
        orders.add(order1);
        orders.add(order2);
        Item item1 = new Item();
        Item item2 = new Item();
        item1.setAmount(2);
        item2.setAmount(2);
        order1.setItem(item1);
        order2.setItem(item2);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // then
        orderService.fulfillOrders(userId);
        verify(orderRepository).deleteAll(orders);
        verify(itemRepository).saveAll(any());
        assertEquals(1, item1.getAmount());
        assertEquals(1, item2.getAmount());
    }

    @Test
    void fulfillOrders_whenOneOrderCanNotBeFulfilled_throwsApiException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        Order order2 = new Order();
        order1.setId(1);
        order2.setId(2);
        order1.setAmount(3);
        order2.setAmount(1);
        orders.add(order1);
        orders.add(order2);
        Item item1 = new Item();
        Item item2 = new Item();
        item1.setName("item name 1");
        item2.setName("item name 2");
        item1.setAmount(2);
        item2.setAmount(2);
        order1.setItem(item1);
        order2.setItem(item2);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.fulfillOrders(userId));
        String errorMessage0 = "order: 1 Ordered amount (3) exceeds available stock (2) for item: item name 1.";
        assertEquals(errorMessage0, apiException.getErrors().get(0));
        assertEquals(2, item1.getAmount());
        assertEquals(2, item2.getAmount());
    }

    @Test
    void fulfillOrders_whenAllOrdersCanNotBeFulfilled_throwsException() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        Order order2 = new Order();
        order1.setId(1);
        order2.setId(2);
        order1.setAmount(3);
        order2.setAmount(3);
        orders.add(order1);
        orders.add(order2);
        Item item1 = new Item();
        Item item2 = new Item();
        item1.setName("item name 1");
        item2.setName("item name 2");
        item1.setAmount(2);
        item2.setAmount(2);
        order1.setItem(item1);
        order2.setItem(item2);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> orderService.fulfillOrders(userId));
        String errorMessage0 = "order: 1 Ordered amount (3) exceeds available stock (2) for item: item name 1.";
        assertEquals(errorMessage0, apiException.getErrors().get(0));
        String errorMessage1 = "order: 2 Ordered amount (3) exceeds available stock (2) for item: item name 2.";
        assertEquals(errorMessage1, apiException.getErrors().get(1));
        assertEquals(2, item1.getAmount());
        assertEquals(2, item2.getAmount());
    }

    @Test
    void fulfillOrders_whenNoOrdersExistsForUser_doNothing() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        List<Order> noOrders = Collections.emptyList();

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(noOrders);

        // then
        orderService.fulfillOrders(userId);
        verify(orderRepository).deleteAll(noOrders);
        verify(itemRepository).saveAll(any());
    }

    @Test
    void fulfillOrders_whenExistingAmountEqualsAmountToOrder_fulfillOrders() {
        // given
        User user = new User();
        int userId = 1;
        user.setId(userId);
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        Order order2 = new Order();
        order1.setAmount(2);
        order2.setAmount(2);
        orders.add(order1);
        orders.add(order2);
        Item item1 = new Item();
        Item item2 = new Item();
        item1.setAmount(2);
        item2.setAmount(2);
        order1.setItem(item1);
        order2.setItem(item2);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // then
        List<Order> fulfilledOrders = orderService.fulfillOrders(userId);
        assertEquals(orders, fulfilledOrders);
        verify(orderRepository).deleteAll(orders);
        verify(itemRepository).saveAll(any());
        assertEquals(0, item1.getAmount());
        assertEquals(0, item2.getAmount());
    }
}
