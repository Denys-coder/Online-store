package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class ItemPatchMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void itemPatchDTOToItem(ItemPatchDTO itemPatchDTO, @MappingTarget Item item);
}
