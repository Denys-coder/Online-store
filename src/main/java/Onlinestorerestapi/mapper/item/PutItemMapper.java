package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.PutItemDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.item.util.ItemMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class PutItemMapper {

    @Mapping(target = "logoName", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtil.generateUUID())")
    @Mapping(target = "imageNames", source = "imageAmount", qualifiedByName = "populateImageNames")
    public abstract void putItemDTOToItem(PutItemDTO putItemDTO, @MappingTarget Item item, int imageAmount);

    @Named("populateImageNames")
    protected Set<String> populateImageNames(int imageAmount) {
        return ItemMapperUtil.populateImageNames(imageAmount);
    }
}
