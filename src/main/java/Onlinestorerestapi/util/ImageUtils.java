package Onlinestorerestapi.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ImageUtils {
    private final Path imagesDirectory;

    public ImageUtils(Environment environment) {
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
                Path path = saveImage(images.get(i), imageNames.get(i));
                savedFiles.add(path);
            }
        } catch (IOException e) {
            rollbackSavedFiles(savedFiles);
            throw new UncheckedIOException("Failed to save images to folder", e);
        }
    }

    public void swapImages(List<String> oldImageNames, List<MultipartFile> newImages, List<String> newImageNames) {
        validateMatchingSizes(newImages.size(), newImageNames.size());
        List<Path> newSavedFiles = new ArrayList<>();
        Map<Path, byte[]> oldBackups = backupFiles(oldImageNames);

        try {
            for (String name : oldImageNames) {
                Files.deleteIfExists(resolveImagePath(name));
            }
            for (int i = 0; i < newImages.size(); i++) {
                Path path = saveImage(newImages.get(i), newImageNames.get(i));
                newSavedFiles.add(path);
            }
        } catch (IOException e) {
            rollbackSavedFiles(newSavedFiles);
            restoreBackups(oldBackups);
            throw new UncheckedIOException("Failed to swap images", e);
        }
    }

    public void deleteImagesFromFolder(List<String> imageNames) {
        Map<Path, byte[]> backups = backupFiles(imageNames);

        try {
            for (Path path : backups.keySet()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            restoreBackups(backups);
            throw new UncheckedIOException("Failed to delete all images. Rolled back changes.", e);
        }
    }

    // ======= PRIVATE HELPERS =======

    private void validateMatchingSizes(int size1, int size2) {
        if (size1 != size2) {
            throw new IllegalArgumentException("List sizes must match.");
        }
    }

    private Path resolveImagePath(String imageName) {
        return imagesDirectory.resolve(imageName).normalize();
    }

    private Path saveImage(MultipartFile image, String name) throws IOException {
        Path path = resolveImagePath(name);
        Files.write(path, image.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return path;
    }

    private Map<Path, byte[]> backupFiles(List<String> imageNames) {
        Map<Path, byte[]> backups = new HashMap<>();
        for (String name : imageNames) {
            Path path = resolveImagePath(name);
            if (Files.exists(path)) {
                try {
                    backups.put(path, Files.readAllBytes(path));
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to backup file: " + name, e);
                }
            }
        }
        return backups;
    }

    private void restoreBackups(Map<Path, byte[]> backups) {
        for (Map.Entry<Path, byte[]> entry : backups.entrySet()) {
            try {
                Files.write(entry.getKey(), entry.getValue(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                System.out.println("Failed to restore backup: " + entry.getKey() + " - " + ex.getMessage());
            }
        }
    }

    private void rollbackSavedFiles(List<Path> savedFiles) {
        for (Path path : savedFiles) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                System.out.println("Rollback failed for: " + path + " - " + ex.getMessage());
            }
        }
    }
}
