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

    @Test
    void combineLogoAndPictures_includeBothLogoAndPictures() {
        // given
        MultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", new byte[0]);
        MultipartFile pic1 = new MockMultipartFile("pic1", "pic1.png", "image/png", new byte[0]);
        MultipartFile pic2 = new MockMultipartFile("pic2", "pic2.png", "image/png", new byte[0]);
        List<MultipartFile> pictures = List.of(pic1, pic2);

        // then
        List<MultipartFile> result = imageUtils.combineLogoAndPictures(logo, pictures);
        assertEquals(List.of(logo, pic1, pic2), result);
    }

    @Test
    void combineLogoAndPictures_includeOnlyLogo() {
        // given
        MultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", new byte[0]);

        // then
        List<MultipartFile> result = imageUtils.combineLogoAndPictures(logo, null);
        assertEquals(List.of(logo), result);
    }

    @Test
    void combineLogoAndPictures_includeOnlyPictures() {
        // given
        MultipartFile pic1 = new MockMultipartFile("pic1", "pic1.png", "image/png", new byte[0]);
        MultipartFile pic2 = new MockMultipartFile("pic2", "pic2.png", "image/png", new byte[0]);
        List<MultipartFile> pictures = List.of(pic1, pic2);

        // then
        List<MultipartFile> result = imageUtils.combineLogoAndPictures(null, pictures);
        assertEquals(List.of(pic1, pic2), result);
    }

    @Test
    void combineLogoAndPictures_includeNeitherLogoNorPictures() {
        // given
        MultipartFile logo = null;
        List<MultipartFile> pictures = null;

        // then
        List<MultipartFile> result = imageUtils.combineLogoAndPictures(logo, pictures);
        assertTrue(result.isEmpty());
    }

    @Test
    void combineLogoAndPictures_emptyPicturesListWithLogo() {
        // given
        MultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", new byte[0]);
        List<MultipartFile> pictures = List.of();

        // then
        List<MultipartFile> result = imageUtils.combineLogoAndPictures(logo, pictures);
        assertEquals(List.of(logo), result);
    }

    @Test
    void combineLogoAndPictures_emptyPicturesListOnly() {
        // given
        List<MultipartFile> pictures = List.of();

        // then
        List<MultipartFile> result = imageUtils.combineLogoAndPictures(null, pictures);
        assertTrue(result.isEmpty());
    }

    @Test
    void combineLogoAndPictureNames_includeBothLogoAndPictures() {
        // given
        String logoName = "logo.png";
        List<String> pictureNames = List.of("pic1.png", "pic2.png");

        // then
        List<String> result = imageUtils.combineLogoAndPictureNames(logoName, pictureNames);
        assertEquals(List.of("logo.png", "pic1.png", "pic2.png"), result);
    }

    @Test
    void combineLogoAndPictureNames_includeOnlyLogo() {
        // given
        String logoName = "logo.png";
        List<String> pictureNames = null;

        // then
        List<String> result = imageUtils.combineLogoAndPictureNames(logoName, pictureNames);
        assertEquals(List.of("logo.png"), result);
    }

    @Test
    void combineLogoAndPictureNames_includeOnlyPictures() {
        // given
        String logoName = null;
        List<String> pictureNames = List.of("pic1.png", "pic2.png");

        // then
        List<String> result = imageUtils.combineLogoAndPictureNames(logoName, pictureNames);
        assertEquals(List.of("pic1.png", "pic2.png"), result);
    }

    @Test
    void combineLogoAndPictureNames_includeNeitherLogoNorPictures() {
        // given
        String logoName = null;
        List<String> pictureNames = null;

        // then
        List<String> result = imageUtils.combineLogoAndPictureNames(logoName, pictureNames);
        assertTrue(result.isEmpty());
    }

    @Test
    void combineLogoAndPictureNames_emptyPictureListWithLogo() {
        // given
        String logoName = "logo.png";
        List<String> pictureNames = List.of();

        // then
        List<String> result = imageUtils.combineLogoAndPictureNames(logoName, pictureNames);
        assertEquals(List.of("logo.png"), result);
    }
}
