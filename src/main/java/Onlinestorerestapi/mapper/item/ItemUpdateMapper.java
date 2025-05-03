package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ItemUpdateMapper {

    @Mapping(target = "logoName", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtils.generateUUID())")
    @Mapping(target = "imageNames", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtils.populateImageNames(imageAmount))")
    public abstract void itemUpdateDTOToItem(ItemUpdateDTO itemUpdateDTO, @MappingTarget Item item, int imageAmount);

}
