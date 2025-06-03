package Onlinestorerestapi.service.item;

import Onlinestorerestapi.service.image.ImageStorageService;
import Onlinestorerestapi.util.FileStorageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    FileStorageUtils fileStorageUtils;

    @InjectMocks
    ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        imageStorageService.setImagesDirectory(tempDir.toString());
    }

    @Test
    void saveImagesToFolder_whenSizeMismatch_throwsException() {

        // given
        List<MultipartFile> images = new ArrayList<>();
        images.add(Mockito.mock(MultipartFile.class));
        List<String> imageNames = new ArrayList<>();

        // when

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> imageStorageService.saveImagesToFolder(images, imageNames));
        assertEquals("List sizes must match.", illegalArgumentException.getMessage());
    }

    @Test
    void saveImagesToFolder_whenSaveImageFails_throwsException() throws IOException {

        // given
        List<MultipartFile> images = new ArrayList<>();
        images.add(Mockito.mock(MultipartFile.class));
        images.add(Mockito.mock(MultipartFile.class));
        List<String> imageNames = new ArrayList<>();
        imageNames.add("Image name 1");
        imageNames.add("Image name 2");
        Path savedFile = Paths.get(tempDir.toString(), imageNames.get(0));
        // ArgumentCaptor to capture what was passed to rollbackSavedImages
        ArgumentCaptor<List<Path>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        // when
        when(fileStorageUtils.saveImage(images.get(0), imageNames.get(0))).thenReturn(savedFile);
        when(fileStorageUtils.saveImage(images.get(1), imageNames.get(1))).thenThrow(IOException.class);

        // then
        UncheckedIOException uncheckedIOException = assertThrows(UncheckedIOException.class, () -> imageStorageService.saveImagesToFolder(images, imageNames));
        assertEquals("Failed to save images to folder", uncheckedIOException.getMessage());
        verify(fileStorageUtils).rollbackSavedImages(argumentCaptor.capture());
        assertEquals(savedFile, argumentCaptor.getValue().get(0));

    }

    @Test
    void saveImagesToFolder_successfullySaveImages() throws IOException {

        // given
        List<MultipartFile> images = new ArrayList<>();
        images.add(Mockito.mock(MultipartFile.class));
        List<String> imageNames = new ArrayList<>();
        imageNames.add("Image name 1");
        Path savedFile = Paths.get(tempDir.toString(), imageNames.get(0));

        // when
        when(fileStorageUtils.saveImage(images.get(0), imageNames.get(0))).thenReturn(savedFile);

        // then
        imageStorageService.saveImagesToFolder(images, imageNames);
    }
}
