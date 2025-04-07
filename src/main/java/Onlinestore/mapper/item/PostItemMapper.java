package Onlinestore.mapper.item;

import Onlinestore.dto.item.PostItemDTO;
import Onlinestore.entity.Item;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Mapper(componentModel = "spring")
public abstract class PostItemMapper {

    UUID uuid = UUID.randomUUID();

    @Mapping(target = "logoName", expression = "java(uuid.randomUUID().toString())")
    @Mapping(target = "imageNames", source = "imageLength", qualifiedByName = "populateImageNames")
    public abstract Item postItemDTOToItem(PostItemDTO postItemDTO, int imageLength);

    @Named("populateImageNames")
    protected Set<String> populateImageNames(int imageLength) {
        Set<String> imageNames = new HashSet<>();
        while (imageNames.size() < imageLength) {
            String uuid = UUID.randomUUID().toString();
            imageNames.add(uuid);
        }
        return imageNames;
    }
}
