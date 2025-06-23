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
    @Mapping(target = "pictureNames", expression = "java(populatePictureNames(pictureAmount))")
    public abstract Item itemCreateDTOToItem(ItemCreateDTO itemCreateDTO, int pictureAmount);

    @Mapping(target = "logoName", expression = "java(generateUUID())")
    @Mapping(target = "pictureNames", expression = "java(populatePictureNames(pictureAmount))")
    public abstract void itemUpdateDTOToItem(ItemUpdateDTO itemUpdateDTO, @MappingTarget Item item, int pictureAmount);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchItemBase(ItemPatchDTO dto, @MappingTarget Item item);

    public void itemPatchDTOToItem(ItemPatchDTO itemPatchDTO, @MappingTarget Item item, MultipartFile logo, List<MultipartFile> pictures) {
        patchItemBase(itemPatchDTO, item);
        if (logo != null) {
            item.setLogoName(generateUUID());
        }
        if (pictures != null) {
            item.setPictureNames(populatePictureNames(pictures.size()));
        }
    }

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public List<String> populatePictureNames(int pictureAmount) {
        if (pictureAmount < 0) {
            throw new IllegalArgumentException("pictureAmount parameter could not be less than 0");
        }
        List<String> pictureNames = new ArrayList<>();
        while (pictureNames.size() < pictureAmount) {
            pictureNames.add(UUID.randomUUID().toString());
        }
        return pictureNames;
    }

}
