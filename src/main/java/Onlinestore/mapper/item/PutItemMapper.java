package Onlinestore.mapper.item;

import Onlinestore.dto.item.PutItemDTO;
import Onlinestore.entity.Item;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class PutItemMapper {

    @Mapping(target = "logoName", expression = "java(generateUUID())")
    @Mapping(target = "imageNames", source = "imageLength", qualifiedByName = "populateImageNames")
    public abstract void putItemDTOToItem(PutItemDTO dto, @MappingTarget Item item, int imageLength);

    @Named("populateImageNames")
    protected Set<String> populateImageNames(int imageLength) {
        Set<String> imageNames = new HashSet<>();
        while (imageNames.size() < imageLength) {
            imageNames.add(UUID.randomUUID().toString());
        }
        return imageNames;
    }

    protected String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
