package Onlinestorerestapi.mapper;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

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
    public abstract void patchItemBase(ItemPatchDTO dto, @MappingTarget Item item);

    public void itemPatchDTOToItem(ItemPatchDTO itemPatchDTO, @MappingTarget Item item, MultipartFile logo, List<MultipartFile> images) {
        patchItemBase(itemPatchDTO, item);
        if (logo != null) {
            item.setLogoName(generateUUID());
        }
        if (images != null) {
            item.setImageNames(populateImageNames(images.size()));
        }
    }

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public List<String> populateImageNames(int imageAmount) {
        List<String> imageNames = new ArrayList<>();
        while (imageNames.size() < imageAmount) {
            imageNames.add(UUID.randomUUID().toString());
        }
        return imageNames;
    }

}
