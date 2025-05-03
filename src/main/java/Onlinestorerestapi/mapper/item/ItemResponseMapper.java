package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ItemResponseMapper {
    @Mapping(target = "ordered", source = "ordered")
    public abstract ItemResponseDTO itemToItemResponseDTO(Item item, boolean ordered);
}
