package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.exception.ApiException;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.image.ImageStorageService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemResponseBuilderService itemResponseBuilderService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ImageUtils imageUtils;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemResponseDTO_whenItemDoesNotExists_throwsApiException() {
        // given
        String exceptionName = "No such item";

        // when
        when(itemRepository.findById(999)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, exceptionName));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.getItemResponseDTO(999));
        assertEquals(exceptionName, apiException.getMessage());
    }

    @Test
    void getItemResponseDTO_whenValidRequest_returnsGetResponse() {
        // given
        int itemId = 1;
        Item item = new Item();
        item.setId(1);
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setId(itemId);
        List<ItemResponseDTO> itemResponseDTOs = new ArrayList<>();
        itemResponseDTOs.add(itemResponseDTO);

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemResponseBuilderService.getItemResponseDTOsByItems(List.of(item))).thenReturn(itemResponseDTOs);

        // then
        assertEquals(1, itemService.getItemResponseDTO(itemId).getId());
    }

    @Test
    void createItem_whenItemNameExists_throwsApiException() {
        // given
        String itemName = "Not unique name";
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setName(itemName);
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> pictures = new ArrayList<>();

        // when
        when(itemRepository.existsByName(itemName)).thenReturn(true);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.createItem(itemCreateDTO, logo, pictures));
        assertEquals("Item name should be unique", apiException.getMessage());
    }

    @Test
    void createItem_whenItemNameUnique_createsItem() {
        // given
        String itemName = "Unique name";
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setName(itemName);
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> pictures = new ArrayList<>();
        pictures.add(mock(MultipartFile.class));
        Item item = new Item();
        item.setName(itemName);
        item.setLogoName("logo name");
        item.setPictureNames(List.of("picture name 1", "picture name 2"));
        List<MultipartFile> logoAndPictures = new ArrayList<>();
        logoAndPictures.add(mock(MultipartFile.class));
        logoAndPictures.add(mock(MultipartFile.class));
        List<String> logoAndPictureNames = new ArrayList<>();
        logoAndPictureNames.add(item.getLogoName());
        logoAndPictureNames.addAll(item.getPictureNames());
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setName(itemName);

        // when
        when(itemRepository.existsByName(itemName)).thenReturn(false);
        when(itemMapper.itemCreateDTOToItem(itemCreateDTO, pictures.size())).thenReturn(item);
        when(imageUtils.combineLogoAndPictures(logo, pictures)).thenReturn(logoAndPictures);
        when(imageUtils.combineLogoAndPictureNames(item.getLogoName(), item.getPictureNames())).thenReturn(logoAndPictureNames);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.itemToItemResponseDTO(item, false)).thenReturn(itemResponseDTO);

        // then
        assertEquals(item.getName(), itemService.createItem(itemCreateDTO, logo, pictures).getName());
        verify(itemRepository).save(item);
        verify(imageStorageService).saveImagesToFolder(logoAndPictures, logoAndPictureNames);
    }

    @Test
    void updateItem_whenIdsMismatch_throwsApiException() {
        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(2);
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> pictures = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.updateItem(itemId, itemUpdateDTO, logo, pictures));
        assertEquals("Item id in the path and in the body should match", apiException.getMessage());
    }

    @Test
    void updateItem_whenNameNotUniqueAndNotSame_throwsApiException() {
        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(1);
        String itemUpdateDTOName = "Not unique name";
        itemUpdateDTO.setName(itemUpdateDTOName);
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> pictures = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));
        Item anoterItem = Mockito.mock(Item.class);
        anoterItem.setName("Another name");

        // when
        when(itemRepository.existsByName(itemUpdateDTOName)).thenReturn(true);
        when(itemRepository.findById(1)).thenReturn(Optional.of(anoterItem));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.updateItem(itemId, itemUpdateDTO, logo, pictures));
        assertEquals("Item name should be unique or the same", apiException.getMessage());
    }

    @Test
    void updateItem_whenItemNotFound_throwsApiException() {
        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(1);
        String itemUpdateDTOName = "Same name";
        itemUpdateDTO.setName(itemUpdateDTOName);
        MultipartFile logo = Mockito.mock(MultipartFile.class);
        List<MultipartFile> pictures = List.of(Mockito.mock(MultipartFile.class), Mockito.mock(MultipartFile.class));
        String exceptionName = "No such item";

        // when
        when(itemRepository.existsByName(itemUpdateDTOName)).thenReturn(true);
        when(itemRepository.findById(1)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, exceptionName));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.updateItem(itemId, itemUpdateDTO, logo, pictures));
        assertEquals(exceptionName, apiException.getMessage());
    }

    @Test
    void updateItem_updatesItemAndSwapsImages() {
        // given
        int itemId = 1;
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setId(itemId);
        MultipartFile logo = mock(MultipartFile.class);
        List<MultipartFile> pictures = new ArrayList<>();
        pictures.add(mock(MultipartFile.class));
        pictures.add(mock(MultipartFile.class));
        String updateItemDTOName = "Unique name";
        itemUpdateDTO.setName(updateItemDTOName);
        Item item = new Item();
        item.setId(1);
        item.setLogoName("logo name");
        item.setPictureNames(List.of("picture name 1", "picture name 2"));
        List<String> oldLogoAndPictureNames = new ArrayList<>();
        List<MultipartFile> newLogoAndPictures = new ArrayList<>();
        newLogoAndPictures.add(logo);
        newLogoAndPictures.addAll(pictures);
        List<String> newLogoAndPictureNames = new ArrayList<>();
        newLogoAndPictureNames.add(item.getLogoName());
        newLogoAndPictureNames.addAll(item.getPictureNames());

        // when
        when(itemRepository.existsByName(updateItemDTOName)).thenReturn(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(imageUtils.combineExistingLogoAndPictureNames(item, true, true)).thenReturn(oldLogoAndPictureNames);
        doNothing().when(itemMapper).itemUpdateDTOToItem(itemUpdateDTO, item, pictures.size());
        when(itemRepository.save(item)).thenReturn(item);
        when(imageUtils.combineLogoAndPictures(logo, pictures)).thenReturn(newLogoAndPictures);
        when(imageUtils.combineLogoAndPictureNames(item.getLogoName(), item.getPictureNames())).thenReturn(newLogoAndPictureNames);
        doNothing().when(imageStorageService).swapImages(oldLogoAndPictureNames, newLogoAndPictures, newLogoAndPictureNames);

        // then
        Item updatedItem = itemService.updateItem(itemId, itemUpdateDTO, logo, pictures);
        assertEquals(itemId, updatedItem.getId());
        assertEquals(itemId, itemRepository.save(item).getId());
        verify(imageStorageService).swapImages(oldLogoAndPictureNames, newLogoAndPictures, newLogoAndPictureNames);
    }

    @Test
    void patchItem_whenIdsMismatch_throwsApiException() {
        // given
        int itemId = 1;
        ItemPatchDTO itemPatchDTO = new ItemPatchDTO();
        itemPatchDTO.setId(2);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.patchItem(itemId, itemPatchDTO, null, null));
        assertEquals("Item id in the path and in the body should match", apiException.getMessage());
    }

    @Test
    void patchItem_whenNameNotUniqueAndNotSame_throwsApiException() {
        // given
        int itemId = 1;
        ItemPatchDTO itemPatchDTO = new ItemPatchDTO();
        itemPatchDTO.setId(1);
        itemPatchDTO.setName("Not unique name");

        // when
        when(itemRepository.existsByName("Not unique name")).thenReturn(true);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.patchItem(itemId, itemPatchDTO, null, null));
        assertEquals("Item name should be unique or the same", apiException.getMessage());
    }

    @Test
    void patchItem_whenItemNotFound_throwsApiException() {
        // given
        int itemId = 1;
        ItemPatchDTO itemPatchDTO = new ItemPatchDTO();
        itemPatchDTO.setId(1);
        itemPatchDTO.setName("Unique name");
        String exceptionMessage = "No such item";

        // when
        when(itemRepository.existsByName("Unique name")).thenReturn(false);
        when(itemRepository.findById(1)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, exceptionMessage));

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> itemService.patchItem(itemId, itemPatchDTO, null, null));
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    void patchItem_whenLogoIsNotNullAndPicturesIsNotNull_patchesItemAndSwapsImages() {
        // given
        int itemId = 1;
        ItemPatchDTO itemPatchDTO = new ItemPatchDTO();
        itemPatchDTO.setId(itemId);
        MultipartFile logo = mock(MultipartFile.class);
        List<MultipartFile> pictures = new ArrayList<>();
        pictures.add(mock(MultipartFile.class));
        pictures.add(mock(MultipartFile.class));
        String patchItemDTOName = "Unique name";
        itemPatchDTO.setName(patchItemDTOName);
        Item item = new Item();
        item.setId(1);
        item.setLogoName("logo name");
        item.setPictureNames(List.of("picture name 1", "picture name 2"));
        List<String> oldLogoAndPictureNames = new ArrayList<>();
        List<MultipartFile> newLogoAndPictures = new ArrayList<>();
        newLogoAndPictures.add(logo);
        newLogoAndPictures.addAll(pictures);
        List<String> newLogoAndPictureNames = new ArrayList<>();
        newLogoAndPictureNames.add(item.getLogoName());
        newLogoAndPictureNames.addAll(item.getPictureNames());

        // when
        when(itemRepository.existsByName(patchItemDTOName)).thenReturn(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(imageUtils.combineExistingLogoAndPictureNames(item, true, true)).thenReturn(oldLogoAndPictureNames);
        doNothing().when(itemMapper).itemPatchDTOToItem(itemPatchDTO, item, logo, pictures);
        when(itemRepository.save(item)).thenReturn(item);
        when(imageUtils.combineLogoAndPictures(logo, pictures)).thenReturn(newLogoAndPictures);
        when(imageUtils.combineLogoAndPictureNames(item.getLogoName(), item.getPictureNames())).thenReturn(newLogoAndPictureNames);
        doNothing().when(imageStorageService).swapImages(oldLogoAndPictureNames, newLogoAndPictures, newLogoAndPictureNames);

        // then
        itemService.patchItem(itemId, itemPatchDTO, logo, pictures);
        assertEquals(itemId, itemRepository.save(item).getId());
        verify(imageStorageService).swapImages(oldLogoAndPictureNames, newLogoAndPictures, newLogoAndPictureNames);
    }

    @Test
    void patchItem_whenLogoIsNullAndPicturesIsNull_patchesItemAndSwapsImages() {
        // given
        int itemId = 1;
        ItemPatchDTO itemPatchDTO = new ItemPatchDTO();
        itemPatchDTO.setId(itemId);
        String itemName = "Unique name";
        itemPatchDTO.setName(itemName);
        Item item = new Item();
        item.setId(itemId);
        List<String> oldLogoAndPicturesNames = new ArrayList<>();
        List<MultipartFile> newLogoAndPictures = new ArrayList<>();
        List<String> newLogoAndPictureNames = new ArrayList<>();

        // when
        when(itemRepository.existsByName(itemName)).thenReturn(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(imageUtils.combineExistingLogoAndPictureNames(item, false, false)).thenReturn(oldLogoAndPicturesNames);
        doNothing().when(itemMapper).itemPatchDTOToItem(itemPatchDTO, item, null, null);
        when(itemRepository.save(item)).thenReturn(item);
        when(imageUtils.combineLogoAndPictures(null, null)).thenReturn(newLogoAndPictures);
        when(imageUtils.combineLogoAndPictureNames(null, Collections.emptyList())).thenReturn(newLogoAndPictureNames);
        doNothing().when(imageStorageService).swapImages(oldLogoAndPicturesNames, newLogoAndPictures, newLogoAndPictureNames);

        // then
        itemService.patchItem(itemId, itemPatchDTO, null, null);
        assertEquals(item, itemRepository.save(item));
        verify(imageStorageService).swapImages(oldLogoAndPicturesNames, newLogoAndPictures, newLogoAndPictureNames);
    }

    @Test
    void deleteItem_whenItemNotFound_throwsApiException() {
        // given
        int itemId = 1;
        String exceptionMessage = "No such item";

        // when
        when(itemRepository.findById(itemId)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, exceptionMessage));

        // then
        ApiException exception = assertThrows(ApiException.class, () -> itemService.deleteItem(itemId));
        assertEquals(exceptionMessage, exception.getMessage());
    }


    @Test
    void deleteItem_whenValidRequest_deletesItem() {
        // given
        int itemId = 1;
        Item item = new Item();
        item.setId(itemId);
        List<String> logoAndPictureNamesToDelete = new ArrayList<>();

        // when
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // then
        itemService.deleteItem(itemId);
        verify(orderRepository).deleteOrdersByItem(item);
        verify(itemRepository).deleteById(itemId);
        verify(imageStorageService).deleteImagesFromFolder(logoAndPictureNamesToDelete);
    }
}
