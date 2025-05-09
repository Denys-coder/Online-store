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

        try {
            Files.createDirectories(this.imagesDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create images directory", e);
        }
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
            for (MultipartFile image : images) {
                String imageName = nameIterator.next();
                Path savedPath = saveImage(image, imageName);
                savedFiles.add(savedPath);
            }
        } catch (IOException e) {
            rollbackSavedFiles(savedFiles);
            throw new UncheckedIOException("Failed to save images to folder", e);
        }
    }

    public void swapImages(List<String> allOldImageNames, List<MultipartFile> allImages, List<String> allNewImageNames) {
        if (allImages.size() != allNewImageNames.size()) {
            throw new IllegalArgumentException("Number of new images must match number of new image names.");
        }

        List<Path> newSavedFiles = new ArrayList<>();
        Map<Path, byte[]> oldImageBackups = new HashMap<>();
        Iterator<String> newNamesIterator = allNewImageNames.iterator();

        try {
            // Backup old images
            for (String oldImageName : allOldImageNames) {
                Path oldImagePath = resolveImagePath(oldImageName);
                if (Files.exists(oldImagePath)) {
                    oldImageBackups.put(oldImagePath, Files.readAllBytes(oldImagePath));
                }
            }

            // Delete old images
            deleteImagesFromFolder(allOldImageNames);

            // Save new images
            for (MultipartFile newImage : allImages) {
                String newImageName = newNamesIterator.next();
                Path savedPath = saveImage(newImage, newImageName);
                newSavedFiles.add(savedPath);
            }

        } catch (IOException e) {
            rollbackSavedFiles(newSavedFiles);

            for (Map.Entry<Path, byte[]> entry : oldImageBackups.entrySet()) {
                try {
                    Files.write(entry.getKey(), entry.getValue(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException ex) {
                    System.err.println("Failed to restore backup image: " + entry.getKey() + " - " + ex.getMessage());
                }
            }

            throw new UncheckedIOException("Failed to swap images", e);
        }
    }

    public void deleteImagesFromFolder(List<String> imageNames) {
        Map<Path, byte[]> imageBackups = new HashMap<>();
        List<Path> deletedPaths = new ArrayList<>();

        try {
            for (String imageName : imageNames) {
                Path imagePath = resolveImagePath(imageName);

                if (Files.exists(imagePath)) {
                    // Backup image before deleting
                    imageBackups.put(imagePath, Files.readAllBytes(imagePath));

                    // Attempt to delete
                    Files.delete(imagePath);
                    deletedPaths.add(imagePath);
                }
            }
        } catch (IOException e) {
            // Rollback previously deleted images
            for (Path deletedPath : deletedPaths) {
                byte[] backup = imageBackups.get(deletedPath);
                if (backup != null) {
                    try {
                        Files.write(deletedPath, backup, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (IOException ex) {
                        System.err.println("Failed to restore image after failed deletion: " + deletedPath + " - " + ex.getMessage());
                    }
                }
            }

            throw new UncheckedIOException("Failed to delete all images. Rolled back changes.", e);
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

    private void rollbackSavedFiles(List<Path> savedFiles) {
        for (Path path : savedFiles) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                System.err.println("Rollback failed for: " + path + " - " + ex.getMessage());
            }
        }
    }
}
