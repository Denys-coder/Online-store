package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemResponseBuilderServiceTest {

    @Mock
    AuthService authService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    ItemResponseBuilderService itemResponseBuilderService;

    @Test
    void getItemResponseDTOsByItems_whenItemsIsEmptyAndUserIsNotAuthenticated_returnsEmptyItemDTOsList() {
        // given
        List<Item> items = new ArrayList<>();

        // when
        when(authService.isAuthenticated()).thenReturn(false);

        // then
        List<ItemResponseDTO> getItemDTOs = itemResponseBuilderService.getItemResponseDTOsByItems(items);
        assertEquals(0, getItemDTOs.size());
    }

    @Test
    void getItemResponseDTOsByItems_whenItemsIsEmptyAndUserIsAuthenticated_returnsEmptyItemDTOsList() {
        // given
        List<Item> items = new ArrayList<>();
        User user = new User();
        List<Order> orders = new ArrayList<>();

        // when
        when(authService.isAuthenticated()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // then
        List<ItemResponseDTO> getItemDTOs = itemResponseBuilderService.getItemResponseDTOsByItems(items);
        assertEquals(0, getItemDTOs.size());
    }

    @Test
    void getItemResponseDTOsByItems_whenItemsIsNotEmptyAndUserIsNotAuthenticated_returnsEmptyItemDTOsList() {
        // given
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        Item item2 = new Item();
        items.add(item1);
        items.add(item2);
        ItemResponseDTO itemResponseDTO1 = new ItemResponseDTO();
        itemResponseDTO1.setOrdered(false);
        ItemResponseDTO itemResponseDTO2 = new ItemResponseDTO();
        itemResponseDTO2.setOrdered(false);

        // when
        when(authService.isAuthenticated()).thenReturn(false);
        when(itemMapper.itemToItemResponseDTO(item1, false)).thenReturn(itemResponseDTO1);
        when(itemMapper.itemToItemResponseDTO(item2, false)).thenReturn(itemResponseDTO2);

        // then
        List<ItemResponseDTO> itemResponseDTOs = itemResponseBuilderService.getItemResponseDTOsByItems(items);
        assertEquals(false, itemResponseDTOs.get(0).getOrdered());
        assertEquals(false, itemResponseDTOs.get(1).getOrdered());
    }

    @Test
    void getItemResponseDTOsByItems_whenItemsIsNotEmptyAndUserIsAuthenticated_returnsEmptyItemDTOsList() {
        // given
        User user = new User();
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1);
        Item item2 = new Item();
        items.add(item1);
        items.add(item2);
        ItemResponseDTO itemResponseDTO1 = new ItemResponseDTO();
        itemResponseDTO1.setOrdered(true);
        ItemResponseDTO itemResponseDTO2 = new ItemResponseDTO();
        itemResponseDTO2.setOrdered(false);
        Order order1 = new Order();
        order1.setItem(item1);
        List<Order> orders = new ArrayList<>();
        orders.add(order1);

        // when
        when(authService.isAuthenticated()).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(itemMapper.itemToItemResponseDTO(item1, true)).thenReturn(itemResponseDTO1);
        when(itemMapper.itemToItemResponseDTO(item2, false)).thenReturn(itemResponseDTO2);

        // then
        List<ItemResponseDTO> itemResponseDTOs = itemResponseBuilderService.getItemResponseDTOsByItems(items);
        assertEquals(true, itemResponseDTOs.get(0).getOrdered());
        assertEquals(false, itemResponseDTOs.get(1).getOrdered());
    }
}
