package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.PatchItemDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class PatchItemMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchItemDTOToItem(PatchItemDTO patchItemDTO, @MappingTarget Item item);
}
