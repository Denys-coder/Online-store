package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getItemResponseDTO_whenItemExistsAndUserNotAuthenticated_returnsItemWithOrderedFalse() {

        // given
        Item item = new Item();
        item.setId(1);
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setId(1);

        // when
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(authService.isAuthenticated()).thenReturn(false);
        when(itemMapper.itemToItemResponseDTO(item, false)).thenReturn(itemResponseDTO);

        // then
        assertEquals(itemService.getItemResponseDTO(1).getId(), itemResponseDTO.getId());
    }

    @Test
    void getItemResponseDTO_whenItemExistsAndUserOrderedItem_returnsItemWithOrderedTrue() {

        // given
        Item item = new Item();
        item.setId(1);
        User user = new User();
        user.setId(1);
        Order order = new Order(item, 1, user);
        List<Order> orders = List.of(order);
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setId(1);

        // when
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(authService.isAuthenticated()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(itemMapper.itemToItemResponseDTO(item, true)).thenReturn(itemResponseDTO);

        // then
        assertEquals(itemService.getItemResponseDTO(1).getId(), itemResponseDTO.getId());
    }

    @Test
    void getItemResponseDTO_whenItemExistsAndUserNotOrderedItem_returnsItemWithOrderedFalse() {

        // given
        Item requestedItem = new Item();
        requestedItem.setId(1);
        Item orderedItem = new Item();
        orderedItem.setId(2);
        User user = new User();
        user.setId(1);
        Order order = new Order(orderedItem, 1, user);
        List<Order> orders = List.of(order);
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setId(1);

        // when
        when(itemRepository.findById(1)).thenReturn(Optional.of(requestedItem));
        when(authService.isAuthenticated()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(itemMapper.itemToItemResponseDTO(requestedItem, false)).thenReturn(itemResponseDTO);

        // then
        assertEquals(itemService.getItemResponseDTO(1).getId(), itemResponseDTO.getId());
    }

    @Test
    void getItemResponseDTO_whenItemDoesNotExist_throwsApiExceptionWithNotFoundStatus() {

        // when
        when(itemRepository.findById(999)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "No such item"));

        // then
        assertThrows(ApiException.class, () -> itemService.getItemResponseDTO(999));
    }
}
