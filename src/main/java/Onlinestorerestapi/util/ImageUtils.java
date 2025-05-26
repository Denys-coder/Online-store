package Onlinestorerestapi.util;

import Onlinestorerestapi.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageUtils {

    public List<String> combineExistingLogoAndImageNames(Item item, boolean includeLogo, boolean includeImages) {
        List<String> logoAndPictureNames = new ArrayList<>();
        if (includeLogo) {
            logoAndPictureNames.add(item.getLogoName());
        }
        if (includeImages) {
            logoAndPictureNames.addAll(item.getPictureNames());
        }
        return logoAndPictureNames;
    }

    public List<MultipartFile> combineLogoAndImages(MultipartFile logo, List<MultipartFile> images) {
        List<MultipartFile> logoAndImages = new ArrayList<>();
        if (logo != null) logoAndImages.add(logo);
        if (images != null) logoAndImages.addAll(images);
        return logoAndImages;
    }

    public List<String> combineLogoAndImageNames(String logoName, List<String> imageNames) {
        List<String> logoAndImageNames = new ArrayList<>();
        if (logoName != null) logoAndImageNames.add(logoName);
        if (imageNames != null) logoAndImageNames.addAll(imageNames);
        return logoAndImageNames;
    }
}
