package Onlinestorerestapi.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;

@Service
public class ItemUtil {
    private final Environment environment;

    public ItemUtil(Environment environment) {
        this.environment = environment;
    }

    public void saveImageToFolder(MultipartFile logo, String name) {
        saveImagesToFolder(new MultipartFile[]{logo}, Set.of(name));
    }

    public void saveImagesToFolder(MultipartFile[] images, Set<String> imageNames) {
        Path imagesDirectory = Paths.get(environment.getProperty("images.directory"));

        try {
            Files.createDirectories(imagesDirectory);

            Iterator<String> nameIterator = imageNames.iterator();

            for (MultipartFile image : images) {
                String imageName = nameIterator.next();
                Path imagePath = imagesDirectory.resolve(imageName);
                Files.write(imagePath, image.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save images to folder", e);
        }
    }

    public void deleteImageFromFolder(String imageName) {
        deleteImagesFromFolder(Set.of(imageName));
    }

    public void deleteImagesFromFolder(Set<String> imageNames) {
        for (String imageName : imageNames) {
            Path imagesDirectory = Paths.get(environment.getProperty("images.directory"));
            File imageFile = imagesDirectory.resolve(imageName).toFile();
            imageFile.delete();
        }
    }
}
