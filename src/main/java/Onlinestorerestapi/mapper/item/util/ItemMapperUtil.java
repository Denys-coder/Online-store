package Onlinestorerestapi.mapper.item.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemMapperUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static Set<String> populateImageNames(int imageAmount) {
        Set<String> imageNames = new HashSet<>();
        while (imageNames.size() < imageAmount) {
            imageNames.add(UUID.randomUUID().toString());
        }
        return imageNames;
    }
}
