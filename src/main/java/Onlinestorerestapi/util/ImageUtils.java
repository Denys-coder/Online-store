package Onlinestorerestapi.util;

import Onlinestorerestapi.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageUtils {

    public List<String> combineExistingLogoAndPictureNames(Item item, boolean includeLogo, boolean includePictures) {
        List<String> logoAndPictureNames = new ArrayList<>();
        if (includeLogo) {
            logoAndPictureNames.add(item.getLogoName());
        }
        if (includePictures) {
            logoAndPictureNames.addAll(item.getPictureNames());
        }
        return logoAndPictureNames;
    }

    public List<MultipartFile> combineLogoAndPictures(MultipartFile logo, List<MultipartFile> pictures) {
        List<MultipartFile> logoAndPictures = new ArrayList<>();
        if (logo != null) logoAndPictures.add(logo);
        if (pictures != null) logoAndPictures.addAll(pictures);
        return logoAndPictures;
    }

    public List<String> combineLogoAndPictureNames(String logoName, List<String> pictureNames) {
        List<String> logoAndPictureNames = new ArrayList<>();
        if (logoName != null) logoAndPictureNames.add(logoName);
        if (pictureNames != null) logoAndPictureNames.addAll(pictureNames);
        return logoAndPictureNames;
    }
}
