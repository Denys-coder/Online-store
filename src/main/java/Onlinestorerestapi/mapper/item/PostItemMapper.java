package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostItemMapper {

    @Mapping(target = "logoName", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtils.generateUUID())")
    @Mapping(target = "imageNames", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtils.populateImageNames(imageAmount))")
    public abstract Item itemCreateDTOToItem(ItemCreateDTO itemCreateDTO, int imageAmount);

}
