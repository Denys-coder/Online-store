package Onlinestorerestapi.mapper.item;

import Onlinestorerestapi.dto.item.PostItemDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.item.util.ItemMapperUtil;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class PostItemMapper {

    @Mapping(target = "logoName", expression = "java(Onlinestorerestapi.mapper.item.util.ItemMapperUtil.generateUUID())")
    @Mapping(target = "imageNames", source = "imageAmount", qualifiedByName = "populateImageNames")
    public abstract Item postItemDTOToItem(PostItemDTO postItemDTO, int imageAmount);

    @Named("populateImageNames")
    protected Set<String> populateImageNames(int imageAmount) {
        return ItemMapperUtil.populateImageNames(imageAmount);
    }
}