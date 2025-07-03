package Onlinestorerestapi.service.image;

import Onlinestorerestapi.service.FileOperationsService;
import Onlinestorerestapi.service.FileStorageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final FileStorageService fileStorageService;

    private final FileOperationsService fileOperationsService;

    @PreAuthorize("hasRole('ADMIN')")
    public void saveImagesToFolder(List<MultipartFile> images, List<String> imageNames) {
        validateMatchingSizes(images.size(), imageNames.size());
        List<Path> savedFiles = new ArrayList<>();

        try {
            for (int i = 0; i < images.size(); i++) {
                Path path = fileStorageService.saveFiles(images.get(i), imageNames.get(i));
                savedFiles.add(path);
            }
        } catch (IOException e) {
            fileStorageService.deleteFiles(savedFiles);
            throw new UncheckedIOException("Failed to save images to folder", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void swapImages(List<String> oldImageNames, List<MultipartFile> newImages, List<String> newImageNames) {
        validateMatchingSizes(newImages.size(), newImageNames.size());
        List<Path> newSavedFiles = new ArrayList<>();
        Map<Path, byte[]> oldBackups = fileStorageService.getFileBytes(oldImageNames);

        try {
            for (String name : oldImageNames) {
                fileOperationsService.deleteIfExists(Paths.get(imagesDirectory).resolve(name).normalize());
            }
            for (int i = 0; i < newImages.size(); i++) {
                Path path = fileStorageService.saveFiles(newImages.get(i), newImageNames.get(i));
                newSavedFiles.add(path);
            }
        } catch (IOException e) {
            fileStorageService.deleteFiles(newSavedFiles);
            fileStorageService.saveFiles(oldBackups);
            throw new UncheckedIOException("Failed to swap images", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImagesFromFolder(List<String> imageNames) {
        Map<Path, byte[]> backups = fileStorageService.getFileBytes(imageNames);

        try {
            for (Path path : backups.keySet()) {
                fileOperationsService.deleteIfExists(path);
            }
        } catch (IOException e) {
            fileStorageService.saveFiles(backups);
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
