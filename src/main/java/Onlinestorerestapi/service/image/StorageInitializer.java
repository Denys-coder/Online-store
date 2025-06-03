package Onlinestorerestapi.service.image;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class StorageInitializer implements CommandLineRunner {

    private final Environment environment;

    @Override
    public void run(String... args) {
        String logoAndImagesDirectory = environment.getProperty("images.directory");
        if (logoAndImagesDirectory == null || logoAndImagesDirectory.isBlank()) {
            throw new IllegalArgumentException("Property 'images.directory' is not set.");
        }
        Path imagesDirectory = Paths.get(logoAndImagesDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(imagesDirectory); // will not override an existing folder
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create images directory", e);
        }
    }
}
