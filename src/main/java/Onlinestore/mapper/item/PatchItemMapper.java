package Onlinestore.mapper.item;

import Onlinestore.dto.item.PatchItemDTO;
import Onlinestore.entity.Item;
import lombok.NoArgsConstructor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class PatchItemMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchItemDTOToItem(PatchItemDTO dto, @MappingTarget Item item);
}
