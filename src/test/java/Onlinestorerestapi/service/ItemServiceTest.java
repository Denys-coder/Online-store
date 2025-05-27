package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.exception.ApiException;
import Onlinestorerestapi.service.image.ImageStorageService;
import Onlinestorerestapi.service.item.ItemResponseBuilderService;
import Onlinestorerestapi.service.item.ItemService;
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
    private ItemResponseBuilderService itemResponseBuilderService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ImageUtils imageUtils;

    @Mock
    ImageStorageService imageStorageService;

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

        // method parameters
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
    void createItem_whenItemNameExists_shouldThrowException() {

        // given

        // method parameters
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
    void createItem_whenItemNameUnique_shouldCreateItem() {

        // given

        // method parameters
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

        // when
        when(itemRepository.existsByName(itemName)).thenReturn(false);
        when(itemMapper.itemCreateDTOToItem(itemCreateDTO, pictures.size())).thenReturn(item);
        when(imageUtils.combineLogoAndImages(logo, pictures)).thenReturn(logoAndPictures);
        when(imageUtils.combineLogoAndImageNames(item.getLogoName(), item.getPictureNames())).thenReturn(logoAndPictureNames);
        when(itemRepository.save(item)).thenReturn(item);

        // then
        assertEquals(item.getName(), itemService.createItem(itemCreateDTO, logo, pictures).getName());
        verify(itemRepository).save(item);
        verify(imageStorageService).saveImagesToFolder(logoAndPictures, logoAndPictureNames);
    }
}
