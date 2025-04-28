package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.GetItemDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class GetItemMapper {
    @Mapping(target = "ordered", source = "ordered")
    public abstract GetItemDTO itemToGetItemDTO(Item item, boolean ordered);
}
