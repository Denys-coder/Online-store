package Onlinestorerestapi.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageUtilsTest {

    @TempDir
    Path tempDir;

    @InjectMocks
    FileStorageUtils fileStorageUtils;

    @BeforeEach
    public void setUpImagesDirectory() {
        ReflectionTestUtils.setField(fileStorageUtils, "imagesDirectory", tempDir.toString());
    }

    @Test
    public void saveImages_successfullySavesImages() throws IOException {
        // given
        MultipartFile image = mock(MultipartFile.class);
        byte[] fileContent = "file-content".getBytes();
        String imageName = "image 1";

        // when
        when(image.getBytes()).thenReturn(fileContent);

        // then
        fileStorageUtils.saveImage(image, imageName);
        assertTrue(Files.exists(tempDir.resolve(imageName).normalize()));
    }
}
