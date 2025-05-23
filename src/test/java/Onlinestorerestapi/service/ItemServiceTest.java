package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.exception.ApiException;
import Onlinestorerestapi.util.ImageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

    @Mock
    private ImageUtils imageUtils;

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

    @Test
    void createItem_whenItemNameUnique_shouldCreateItem() {

        // given
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setName("Unique name");
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));
        Item item = new Item();
        item.setLogoName("logo name 1");
        item.setImageNames(List.of("image name 1", "image name 2"));
        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(logo);
        allImages.addAll(images);
        List<String> allImageNames = new ArrayList<>();
        allImageNames.add(item.getLogoName());
        allImageNames.addAll(item.getImageNames());

        // when
        when(itemRepository.existsByName(itemCreateDTO.getName())).thenReturn(false);
        when(itemMapper.itemCreateDTOToItem(itemCreateDTO, images.size())).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        doNothing().when(imageUtils).saveImagesToFolder(allImages, allImageNames);

        // then
        assertEquals(item, itemService.createItem(itemCreateDTO, logo, images));
        verify(itemRepository).save(item);
        verify(imageUtils).saveImagesToFolder(allImages, allImageNames);
    }

    @Test
    void createItem_whenItemNameExists_shouldThrowException() {

        // given
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setName("Existing name");
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of();

        // when
        when(itemRepository.existsByName(itemCreateDTO.getName())).thenReturn(true);

        // then
        assertThrows(ApiException.class, () -> itemService.createItem(itemCreateDTO, logo, images));
    }

    @Test
    void updateItem_successfullyUpdatesItemAndSwapsImages() {

        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(1);
        itemUpdateDTO.setName("Unique name");
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));
        Item itemInDb = new Item();
        itemInDb.setName("Unique name");
        itemInDb.setLogoName("old logo1");
        itemInDb.setImageNames(List.of("old image1", "old image2"));
        List<String> oldImages = new ArrayList<>();
        oldImages.add(itemInDb.getLogoName());
        oldImages.addAll(itemInDb.getImageNames());
        List<String> newNames = new ArrayList<>();
        newNames.add("new logo1");
        newNames.addAll(List.of("new image1", "new image2"));
        List<MultipartFile> newImages = new ArrayList<>();
        newImages.add(logo);
        newImages.addAll(images);

        // when
        when(itemRepository.existsByName("Unique name")).thenReturn(true);
        when(itemRepository.findById(1)).thenReturn(Optional.of(itemInDb));
        doAnswer(invocationOnMock -> {
            itemInDb.setLogoName("new logo1");
            itemInDb.setImageNames(List.of("new image1", "new image2"));
            return null;
        }).when(itemMapper).itemUpdateDTOToItem(itemUpdateDTO, itemInDb, images.size());
        when(itemRepository.save(itemInDb)).thenReturn(itemInDb);
        doNothing().when(imageUtils).swapImages(oldImages, newImages, newNames);

        // then
        itemService.updateItem(itemId, itemUpdateDTO, logo, images);
        verify(itemRepository).save(itemInDb);
    }

    @Test
    void updateItem_throwsWhenIdMismatch() {

        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(2);
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));

        // then
        assertThrows(ApiException.class, () -> itemService.updateItem(itemId, itemUpdateDTO, logo, images));
    }

    @Test
    void updateItem_throwsWhenNameNotUnique() {

        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(1);
        itemUpdateDTO.setName("Not unique name");
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));
        Item anoterItem = Mockito.mock(Item.class);
        anoterItem.setName("Another name");

        // when
        when(itemRepository.existsByName("Not unique name")).thenReturn(true);
        when(itemRepository.findById(1)).thenReturn(Optional.of(anoterItem));

        // then
        assertThrows(ApiException.class, () -> itemService.updateItem(itemId, itemUpdateDTO, logo, images));
    }

    @Test
    void updateItem_throwsWhenItemNotFound() {

        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(1);
        itemUpdateDTO.setName("Same name");
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> images = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));

        // when
        when(itemRepository.existsByName("Same name")).thenReturn(true);
        when(itemRepository.findById(1)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "No such item"));

        // then
        assertThrows(ApiException.class, () -> itemService.updateItem(itemId, itemUpdateDTO, logo, images));
    }
}
