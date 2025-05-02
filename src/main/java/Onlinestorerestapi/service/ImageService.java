package Onlinestorerestapi.service;

import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ImageService {

    private final Environment environment;
    private static final Tika tika = new Tika(); // reused instance

    public Resource getImage(String imageName) {
        try {
            Path imagesDirectory = Paths.get(environment.getProperty("images.directory")).normalize();
            Path imagePath = imagesDirectory.resolve(imageName).normalize();

            // Prevent path traversal
            if (!imagePath.startsWith(imagesDirectory)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Don't allow path traversal: " + imagePath);
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

    public String getImageType(Resource image) {
        String imageType;
        try {
            imageType = tika.detect(image.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to detect image type", e);
        }
        return imageType;
    }
}
