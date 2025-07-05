package Onlinestorerestapi.service.image;

import Onlinestorerestapi.exception.ApiException;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    Tika tika;

    @TempDir
    Path tempDir;

    @InjectMocks
    ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        imageService.setImagesDirectory(tempDir.toString());
    }

    @Test
    void getImageDTO_whenAttemptsPathTraversal_throwsApiException() {
        // given
        String imageName = "../../etc/passwd";

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> imageService.getImageDTO(imageName));
        assertEquals("Path traversal not allowed", apiException.getMessage());
    }

    @Test
    void getImageDTO_whenImageNotExists_throwsApiException() {
        // given
        String imageName = "notExistingImage.jpg";

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> imageService.getImageDTO(imageName));
        assertEquals("Specified image does not exist: " + imageName, apiException.getMessage());
    }

    @Test
    void getImageDTO_whenValidRequest_returnsImageResponseDTO() throws IOException {
        // given
        String imageName = "image.webp";
        String contentType = "image/webp";
        try (OutputStream os = Files.newOutputStream(Paths.get(imageService.getImagesDirectory()).resolve(imageName))) {
            os.write("fake image data".getBytes());
        }

        // when
        when(tika.detect(any(InputStream.class))).thenReturn("image/webp");

        // then
        Resource image = new UrlResource(Paths.get(imageService.getImagesDirectory(), imageName).toAbsolutePath().toUri());
        assertEquals(image.contentLength(), imageService.getImageDTO(imageName).getImage().contentLength());
        assertEquals(contentType, imageService.getImageDTO(imageName).getContentType());
    }

    @Test
    void getImageDTO_whenFailsToDetectImageType_throwsApiException() throws IOException {
        // given
        String imageName = "image.webp";
        try (OutputStream os = Files.newOutputStream(Paths.get(imageService.getImagesDirectory()).resolve(imageName))) {
            os.write("fake image data".getBytes());
        }
        String exceptionMessage = "Failed to detect image type";

        // when
        when(tika.detect(any(InputStream.class))).thenThrow(new IOException("Failed to detect image type"));

        // then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> imageService.getImageDTO(imageName));
        assertEquals(exceptionMessage, runtimeException.getMessage());
    }
}
