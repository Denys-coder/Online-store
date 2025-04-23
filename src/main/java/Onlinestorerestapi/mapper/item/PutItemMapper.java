package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.PutItemDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.item.util.ItemMapperUtil;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class PutItemMapper {

    @Mapping(target = "logoName", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtil.generateUUID())")
    @Mapping(target = "imageNames", source = "imageLength", qualifiedByName = "populateImageNames")
    public abstract void putItemDTOToItem(PutItemDTO putItemDTO, @MappingTarget Item item, int imageLength);

    @Named("populateImageNames")
    protected Set<String> populateImageNames(int imageLength) {
        return ItemMapperUtil.populateImageNames(imageLength);
    }
}