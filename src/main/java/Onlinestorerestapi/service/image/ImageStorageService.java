package Onlinestorerestapi.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {
    void saveImagesToFolder(List<MultipartFile> images, List<String> imageNames);

    void swapImages(List<String> oldImageNames, List<MultipartFile> newImages, List<String> newImageNames);

    void deleteImagesFromFolder(List<String> imageNames);
}
