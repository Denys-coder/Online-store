package Onlinestorerestapi.mapper;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ItemMapperTest {

    private final ItemMapper itemMapper;

    @Test
    void itemToItemResponseDTO_orderedTrue_setsFieldCorrectly() {
        // given
        Item item = new Item();
        item.setId(1);
        item.setName("item name");

        // then
        ItemResponseDTO itemResponseDTO = itemMapper.itemToItemResponseDTO(item, true);
        assertEquals(item.getId(), itemResponseDTO.getId());
        assertEquals(item.getName(), itemResponseDTO.getName());
        assertTrue(itemResponseDTO.getOrdered());
    }

    @Test
    void itemToItemResponseDTO_orderedFalse_setsFieldCorrectly() {
        // given
        Item item = new Item();
        item.setId(1);
        item.setName("Item name");

        // then
        ItemResponseDTO dto = itemMapper.itemToItemResponseDTO(item, false);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertFalse(dto.getOrdered());
    }

    @Test
    void itemCreateDTOToItem_setsFieldsCorrectly() {
        // given
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        String itemName = "item name";
        itemCreateDTO.setName(itemName);
        double itemPrice = 10.0;
        itemCreateDTO.setPrice(itemPrice);
        int pictureAmount = 2;

        // then
        Item item = itemMapper.itemCreateDTOToItem(itemCreateDTO, pictureAmount);
        assertEquals(itemName, item.getName());
        assertEquals(itemPrice, item.getPrice());
        assertNotNull(item.getLogoName());
        assertDoesNotThrow(() -> UUID.fromString(item.getLogoName()));
        List<String> pictureNames = item.getPictureNames();
        assertNotNull(pictureNames);
        assertEquals(pictureAmount, pictureNames.size());
        pictureNames.forEach(name -> assertDoesNotThrow(() -> UUID.fromString(name)));
    }
}
