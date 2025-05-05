package Onlinestorerestapi.mapper;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {

    @Mapping(target = "ordered", source = "ordered")
    public abstract ItemResponseDTO itemToItemResponseDTO(Item item, boolean ordered);

    @Mapping(target = "logoName", expression = "java(generateUUID())")
    @Mapping(target = "imageNames", expression = "java(populateImageNames(imageAmount))")
    public abstract Item itemCreateDTOToItem(ItemCreateDTO itemCreateDTO, int imageAmount);

    @Mapping(target = "logoName", expression = "java(generateUUID())")
    @Mapping(target = "imageNames", expression = "java(populateImageNames(imageAmount))")
    public abstract void itemUpdateDTOToItem(ItemUpdateDTO itemUpdateDTO, @MappingTarget Item item, int imageAmount);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void itemPatchDTOToItem(ItemPatchDTO itemPatchDTO, @MappingTarget Item item);

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static List<String> populateImageNames(int imageAmount) {
        List<String> imageNames = new ArrayList<>();
        while (imageNames.size() < imageAmount) {
            imageNames.add(UUID.randomUUID().toString());
        }
        return imageNames;
    }

}
