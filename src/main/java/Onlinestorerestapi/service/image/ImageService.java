package Onlinestorerestapi.service.image;

import Onlinestorerestapi.dto.image.ImageResponseDTO;
import Onlinestorerestapi.exception.ApiException;
import org.apache.tika.Tika;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final Tika tika = new Tika();
    private final Path imageDirectory;

    public ImageService(Environment environment) {

        String logoAndImagesDirectory = environment.getProperty("images.directory");
        if (logoAndImagesDirectory == null || logoAndImagesDirectory.isBlank()) {
            throw new IllegalArgumentException("Property 'images.directory' is not set.");
        }
        this.imageDirectory = Paths.get(logoAndImagesDirectory).toAbsolutePath().normalize();
    }

    public ImageResponseDTO getImageDTO(String imageName) {

        Resource image = getImage(imageName);

        // Detect MIME type
        String contentType = getImageType(image);

        return new ImageResponseDTO(image, contentType);
    }

    // ======= PRIVATE HELPERS =======

    private Resource getImage(String imageName) {
        try {
            Path imagePath = imageDirectory.resolve(imageName).normalize();

            // Prevent path traversal
            if (!imagePath.startsWith(imageDirectory)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Path traversal not allowed");
            }

            // Load resource
            Resource resource = new UrlResource(imagePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Specified image does not exist: " + imageName);
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getImageType(Resource image) {
        String imageType;
        try {
            imageType = tika.detect(image.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to detect image type", e);
        }
        return imageType;
    }
}
