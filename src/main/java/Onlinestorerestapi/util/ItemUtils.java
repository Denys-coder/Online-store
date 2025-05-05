package Onlinestorerestapi.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ItemUtils {
    private final Path imagesDirectory;

    public ItemUtils(Environment environment) {
        String dir = environment.getProperty("images.directory");
        if (dir == null || dir.isBlank()) {
            throw new IllegalArgumentException("Property 'images.directory' is not set.");
        }
        this.imagesDirectory = Paths.get(dir).toAbsolutePath().normalize();
    }

    public void saveImageToFolder(MultipartFile logo, String imageName) {
        saveImagesToFolder(List.of(logo), List.of(imageName));
    }

    public void saveImagesToFolder(List<MultipartFile> images, List<String> imageNames) {

        if (images.size() != imageNames.size()) {
            throw new IllegalArgumentException("Number of images must match number of image names.");
        }

        List<Path> savedFiles = new ArrayList<>();
        Iterator<String> nameIterator = imageNames.iterator();

        try {
            Files.createDirectories(imagesDirectory);

            for (MultipartFile image : images) {
                String imageName = nameIterator.next();
                Path imagePath = imagesDirectory.resolve(imageName).normalize();

                Files.write(imagePath, image.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                savedFiles.add(imagePath);
            }

        } catch (IOException e) {
            rollbackSavedFiles(savedFiles);
            throw new UncheckedIOException("Failed to save images to folder", e);
        }
    }

    private void rollbackSavedFiles(List<Path> savedFiles) {
        for (Path path : savedFiles) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                System.err.println("Rollback failed for: " + path + " - " + ex.getMessage());
            }
        }
    }

    public void deleteImageFromFolder(String imageName) {
        deleteImagesFromFolder(List.of(imageName));
    }

    public void deleteImagesFromFolder(List<String> imageNames) {
        for (String imageName : imageNames) {
            Path imagePath = imagesDirectory.resolve(imageName).normalize();
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + imagePath + " - " + e.getMessage());
            }
        }
    }
}