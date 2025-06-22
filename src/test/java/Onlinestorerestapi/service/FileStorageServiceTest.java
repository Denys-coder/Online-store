package Onlinestorerestapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @InjectMocks
    FileStorageService fileStorageService;

    @Mock
    FileOperationsService fileOperationsService;

    @BeforeEach
    public void setUpImagesDirectory() {
        ReflectionTestUtils.setField(fileStorageService, "imagesDirectory", tempDir.toString());
    }

    @Test
    public void saveFiles_successfullySavesImages() throws IOException {
        // given
        MultipartFile image = mock(MultipartFile.class);
        byte[] fileContent = "file-content".getBytes();
        String imageName = "image 1";

        // when
        when(image.getBytes()).thenReturn(fileContent);

        // then
        fileStorageService.saveFiles(image, imageName);
        assertTrue(Files.exists(tempDir.resolve(imageName).normalize()));
    }

    @Test
    public void getFileBytes_whenPathExistsButCouldNotReadBytes_throwsUncheckedIOException() throws IOException {
        // given
        String fileName = "file name 1";
        List<String> fileNames = List.of(fileName);
        byte[] fileContent = "file-content".getBytes();
        Path path = Paths.get(tempDir.toString()).resolve(fileName).normalize();
        Files.write(path, fileContent);
        String exceptionMessage = "Failed to get bytes of file: " + fileName;

        // when
        when(fileOperationsService.readAllBytes(path)).thenThrow(new IOException(exceptionMessage));

        // then
        UncheckedIOException uncheckedIOException = assertThrows(UncheckedIOException.class, () -> fileStorageService.getFileBytes(fileNames));
        assertEquals(exceptionMessage, uncheckedIOException.getMessage());
    }

    @Test
    public void getFileBytes_successfullyGersFileBytes() throws IOException {
        // given
        String fileName = "file name 1";
        List<String> fileNames = List.of(fileName);
        byte[] fileContent = "file-content".getBytes();
        Path path = Paths.get(tempDir.toString()).resolve(fileName).normalize();
        Files.write(path, fileContent);

        // when
        when(fileOperationsService.readAllBytes(path)).thenReturn(fileContent);

        // then
        Map<Path, byte[]> fileBytes = fileStorageService.getFileBytes(fileNames);
        assertEquals(fileBytes.get(path), fileBytes.get(path));
    }
}
