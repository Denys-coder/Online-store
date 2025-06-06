package Onlinestorerestapi.util;

import Onlinestorerestapi.entity.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ImageUtilsTest {

    @InjectMocks
    ImageUtils imageUtils;

    private Item createItem(String logoName, List<String> pictureNames) {
        Item item = new Item();
        item.setLogoName(logoName);
        item.getPictureNames().addAll(pictureNames);
        return item;
    }

    @Test
    void combineExistingLogoAndPictureNames_includeBothLogoAndPictures() {
        // given
        Item item = createItem("logo.png", List.of("pic1.png", "pic2.png"));

        // then
        List<String> result = imageUtils.combineExistingLogoAndPictureNames(item, true, true);
        assertEquals(List.of("logo.png", "pic1.png", "pic2.png"), result);
    }

    @Test
    void combineExistingLogoAndPictureNames_includeOnlyLogo() {
        // given
        Item item = createItem("logo.png", List.of("pic1.png", "pic2.png"));

        // then
        List<String> result = imageUtils.combineExistingLogoAndPictureNames(item, true, false);
        assertEquals(List.of("logo.png"), result);
    }

    @Test
    void combineExistingLogoAndPictureNames_includeOnlyPictures() {
        // given
        Item item = createItem("logo.png", List.of("pic1.png", "pic2.png"));

        // then
        List<String> result = imageUtils.combineExistingLogoAndPictureNames(item, false, true);
        assertEquals(List.of("pic1.png", "pic2.png"), result);
    }

    @Test
    void combineExistingLogoAndPictureNames_includeNeitherLogoNorPictures() {
        // given
        Item item = createItem("logo.png", List.of("pic1.png", "pic2.png"));

        // then
        List<String> result = imageUtils.combineExistingLogoAndPictureNames(item, false, false);
        assertTrue(result.isEmpty());
    }
}
