package Onlinestore.service;

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
public class ItemService {
    private final Environment environment;

    public ItemService(Environment environment) {
        this.environment = environment;
    }

    public void saveLogoToFolder(MultipartFile logo, String logoName) {

        Path logosDirectory = Paths.get(environment.getProperty("images.directory"));

        try {
            Files.createDirectories(logosDirectory);
            Path logoPath = logosDirectory.resolve(Paths.get(logoName));
            try (OutputStream os = new FileOutputStream(logoPath.toFile())) {
                os.write(logo.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
