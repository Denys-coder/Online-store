package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;

import java.util.List;

public interface ItemResponseBuilderService {
    List<ItemResponseDTO> getItemResponseDTOsByItems(List<Item> items);
}
