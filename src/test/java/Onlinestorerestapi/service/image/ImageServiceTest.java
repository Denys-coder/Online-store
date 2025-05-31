package Onlinestorerestapi.service.image;

import Onlinestorerestapi.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    Environment environment;

    @Test
    void constructor_whenImageDirectoryIsNull() {

        // when
        when(environment.getProperty("images.directory")).thenReturn(null);

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new ImageService(environment));
        assertEquals("Property 'images.directory' is not set.", illegalArgumentException.getMessage());
    }

    @Test
    void constructor_whenImageDirectoryIsBlank() {

        // when
        when(environment.getProperty("images.directory")).thenReturn("");

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new ImageService(environment));
        assertEquals("Property 'images.directory' is not set.", illegalArgumentException.getMessage());
    }

    @Test
    void getImageDTO_whenAttemptPathTraversal_throwsException() {

        // given
        String imageName = "../../etc/passwd";
        String imageDirectory = "src/test/resources/";

        // when
        when(environment.getProperty("images.directory")).thenReturn(imageDirectory);

        // then
        ImageService imageService = new ImageService(environment);
        ApiException apiException = assertThrows(ApiException.class, () -> imageService.getImageDTO(imageName));
        assertEquals("Path traversal not allowed", apiException.getMessage());
    }

    @Test
    void getImageDTO_whenImageNotExists_throwsException() {

        // given
        String imageName = "notExistingImage.jpg";
        String imageDirectory = "src/test/resources/";

        // when
        when(environment.getProperty("images.directory")).thenReturn(imageDirectory);

        // then
        ImageService imageService = new ImageService(environment);
        ApiException apiException = assertThrows(ApiException.class, () -> imageService.getImageDTO(imageName));
        assertEquals("Specified image does not exist: " + imageName, apiException.getMessage());
    }

    @Test
    void getImageDTO_successfullyReturnsImageResponseDTO() throws IOException {

        // given
        String imageName = "image.webp";
        String imageDirectory = "src/test/resources/";
        Resource image = new UrlResource(Paths.get(imageDirectory + imageName).toAbsolutePath().toUri());
        String contentType = "image/webp";

        // when
        when(environment.getProperty("images.directory")).thenReturn(imageDirectory);

        // then
        ImageService imageService = new ImageService(environment);
        assertEquals(image.contentLength(), imageService.getImageDTO(imageName).getImage().contentLength());
        assertEquals(contentType, imageService.getImageDTO(imageName).getContentType());
    }
}
