package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponseDTO getItemResponseDTO(int itemId);

    List<ItemResponseDTO> getItemResponseDTOs();

    ItemResponseDTO createItem(ItemCreateDTO itemCreateDTO, MultipartFile logo, List<MultipartFile> pictures);

    Item updateItem(int itemId, ItemUpdateDTO itemUpdateDTO, MultipartFile logo, List<MultipartFile> pictures);

    Item patchItem(int itemId, ItemPatchDTO itemPatchDTO, MultipartFile logo, List<MultipartFile> pictures);

    void deleteItem(int itemId);
}
