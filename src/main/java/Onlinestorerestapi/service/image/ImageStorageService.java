package Onlinestorerestapi.service.image;

import Onlinestorerestapi.util.FileStorageUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ImageStorageService {

    private final Path imagesDirectory;
    private final FileStorageUtils fileStorageUtils;

    public ImageStorageService(Environment environment, FileStorageUtils fileStorageUtils) {
        this.fileStorageUtils = fileStorageUtils;
        String dir = environment.getProperty("images.directory");
        if (dir == null || dir.isBlank()) {
            throw new IllegalArgumentException("Property 'images.directory' is not set.");
        }
        this.imagesDirectory = Paths.get(dir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.imagesDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create images directory", e);
        }
    }

    public void saveImagesToFolder(List<MultipartFile> images, List<String> imageNames) {
        validateMatchingSizes(images.size(), imageNames.size());
        List<Path> savedFiles = new ArrayList<>();

        try {
            for (int i = 0; i < images.size(); i++) {
                Path path = fileStorageUtils.saveImage(images.get(i), imageNames.get(i), imagesDirectory);
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
        Map<Path, byte[]> oldBackups = fileStorageUtils.backupImages(oldImageNames, imagesDirectory);

        try {
            for (String name : oldImageNames) {
                Files.deleteIfExists(imagesDirectory.resolve(name).normalize());
            }
            for (int i = 0; i < newImages.size(); i++) {
                Path path = fileStorageUtils.saveImage(newImages.get(i), newImageNames.get(i), imagesDirectory);
                newSavedFiles.add(path);
            }
        } catch (IOException e) {
            fileStorageUtils.rollbackSavedImages(newSavedFiles);
            fileStorageUtils.restoreBackups(oldBackups);
            throw new UncheckedIOException("Failed to swap images", e);
        }
    }

    public void deleteImagesFromFolder(List<String> imageNames) {
        Map<Path, byte[]> backups = fileStorageUtils.backupImages(imageNames, imagesDirectory);

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
