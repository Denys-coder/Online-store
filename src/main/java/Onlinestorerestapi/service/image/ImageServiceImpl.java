package Onlinestorerestapi.service.image;

import Onlinestorerestapi.dto.image.ImageResponseDTO;
import Onlinestorerestapi.exception.ApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final Tika tika;

    @Value("${images.directory}")
    @Getter
    @Setter
    private String imagesDirectory;

    public ImageResponseDTO getImageDTO(String imageName) {

        Resource image = getImage(imageName);

        // Detect MIME type
        String contentType = getImageType(image);

        return new ImageResponseDTO(image, contentType);
    }

    // ======= PRIVATE HELPERS =======

    private Resource getImage(String imageName) {
        try {
            Path currentImagesPath = Paths.get(imagesDirectory).resolve(imageName).normalize();

            // Prevent path traversal
            if (!currentImagesPath.startsWith(imagesDirectory)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Path traversal not allowed");
            }

            // Load resource
            Resource resource = new UrlResource(currentImagesPath.toUri());
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
