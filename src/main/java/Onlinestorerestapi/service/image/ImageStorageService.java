package Onlinestorerestapi.service.image;

import Onlinestorerestapi.util.FileStorageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    @Value("${images.directory}")
    @Getter
    @Setter
    private String imagesDirectory;

    private final FileStorageUtils fileStorageUtils;

    public void saveImagesToFolder(List<MultipartFile> images, List<String> imageNames) {
        validateMatchingSizes(images.size(), imageNames.size());
        List<Path> savedFiles = new ArrayList<>();

        try {
            for (int i = 0; i < images.size(); i++) {
                Path path = fileStorageUtils.saveImage(images.get(i), imageNames.get(i));
                savedFiles.add(path);
            }
        } catch (IOException e) {
            fileStorageUtils.rollbackSavedImages(savedFiles);
            throw new UncheckedIOException("Failed to save images to folder", e);
        }
    }

    public void swapImages(List<String> oldImageNames, List<MultipartFile> newImages, List<String> newImageNames) {
        validateMatchingSizes(newImages.size(), newImageNames.size());
        List<Path> newSavedFiles = new ArrayList<>();
        Map<Path, byte[]> oldBackups = fileStorageUtils.backupImages(oldImageNames);

        try {
            for (String name : oldImageNames) {
                Files.deleteIfExists(Paths.get(imagesDirectory).resolve(name).normalize());
            }
            for (int i = 0; i < newImages.size(); i++) {
                Path path = fileStorageUtils.saveImage(newImages.get(i), newImageNames.get(i));
                newSavedFiles.add(path);
            }
        } catch (IOException e) {
            fileStorageUtils.rollbackSavedImages(newSavedFiles);
            fileStorageUtils.restoreBackups(oldBackups);
            throw new UncheckedIOException("Failed to swap images", e);
        }
    }

    public void deleteImagesFromFolder(List<String> imageNames) {
        Map<Path, byte[]> backups = fileStorageUtils.backupImages(imageNames);

        try {
            for (Path path : backups.keySet()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            fileStorageUtils.restoreBackups(backups);
            throw new UncheckedIOException("Failed to delete all images. Rolled back changes.", e);
        }
    }

    // ======= PRIVATE HELPERS =======

    private void validateMatchingSizes(int size1, int size2) {
        if (size1 != size2) {
            throw new IllegalArgumentException("List sizes must match.");
        }
    }

}
