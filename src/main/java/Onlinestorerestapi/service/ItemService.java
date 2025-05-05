package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemHelperService itemHelperService;

    @Transactional(readOnly = true)
    public ItemResponseDTO getItemResponseDTO(int itemId) {

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item not found");
        }
        Item item = itemOptional.get();

        List<Item> items = List.of(item);
        List<ItemResponseDTO> itemResponseDTOs = itemHelperService.getItemResponseDTOsByItems(items);

        return itemResponseDTOs.get(0);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getItemResponseDTOs() {
        return itemHelperService.getItemResponseDTOsByItems(itemRepository.findAll());
    }

}
