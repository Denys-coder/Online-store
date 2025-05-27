package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.exception.ApiException;
import Onlinestorerestapi.service.item.ItemResponseBuilderService;
import Onlinestorerestapi.service.item.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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
    private ItemResponseBuilderService itemResponseBuilderService;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getItemResponseDTO_whenItemDoesNotExist_throwsApiExceptionWithNotFoundStatus() {

        // when
        when(itemRepository.findById(999)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "No such item"));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.getItemResponseDTO(999));
        assertEquals("No such item", apiException.getMessage());
    }

    @Test
    void getItemResponseDTO_successfullyGetResponse() {

        // given
        int itemId = 1;
        Item item = new Item();
        item.setId(1);
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setId(1);
        List<ItemResponseDTO> itemResponseDTOs = new ArrayList<>();
        itemResponseDTOs.add(itemResponseDTO);

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemResponseBuilderService.getItemResponseDTOsByItems(List.of(item))).thenReturn(itemResponseDTOs);

        // then
        assertEquals(1, itemService.getItemResponseDTO(1).getId());
    }


}
