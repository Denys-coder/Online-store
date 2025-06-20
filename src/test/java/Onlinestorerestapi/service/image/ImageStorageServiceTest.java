package Onlinestorerestapi.service.image;

import Onlinestorerestapi.util.FileStorageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    void saveImagesToFolder_whenSizesMismatch_throwsIllegalArgumentException() {
        // given
        List<MultipartFile> images = new ArrayList<>();
        images.add(mock(MultipartFile.class));
        List<String> imageNames = new ArrayList<>();

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> imageStorageService.saveImagesToFolder(images, imageNames));
        assertEquals("List sizes must match.", illegalArgumentException.getMessage());
    }

    @Test
    void saveImagesToFolder_whenSaveImageFails_throwsUncheckedIOException() throws IOException {
        // given
        List<MultipartFile> images = new ArrayList<>();
        images.add(mock(MultipartFile.class));
        images.add(mock(MultipartFile.class));
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
    void saveImagesToFolder_whenValidRequest_savesImages() throws IOException {
        // given
        List<MultipartFile> images = new ArrayList<>();
        images.add(mock(MultipartFile.class));
        List<String> imageNames = new ArrayList<>();
        imageNames.add("Image name 1");
        Path savedFile = Paths.get(tempDir.toString(), imageNames.get(0));

        // when
        when(fileStorageUtils.saveImage(images.get(0), imageNames.get(0))).thenReturn(savedFile);

        // then
        imageStorageService.saveImagesToFolder(images, imageNames);
    }

    @Test
    void swapImages_whenSizesMismatch_throwsIllegalArgumentException() {
        // given
        List<String> oldImageNames = new ArrayList<>();
        List<MultipartFile> newImages = new ArrayList<>();
        List<String> newImageNames = new ArrayList<>();
        newImages.add(mock(MultipartFile.class));

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> imageStorageService.swapImages(oldImageNames, newImages, newImageNames));
        assertEquals("List sizes must match.", illegalArgumentException.getMessage());
    }

    @Test
    void swapImages_whenSaveImageFails_throwsUncheckedIOException() throws IOException {
        // given
        List<String> oldImageNames = new ArrayList<>();
        oldImageNames.add("Old image name 1");
        List<MultipartFile> newImages = new ArrayList<>();
        newImages.add(mock(MultipartFile.class));
        newImages.add(mock(MultipartFile.class));
        List<String> newImageNames = new ArrayList<>();
        newImageNames.add("New image name 1");
        newImageNames.add("New image name 2");
        Map<Path, byte[]> oldBackups = new HashMap<>();
        Path newSavedFile1 = Paths.get(tempDir.toString(), newImageNames.get(0));
        // ArgumentCaptor to capture what was passed to rollbackSavedImages
        ArgumentCaptor<List<Path>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        // when
        when(fileStorageUtils.backupImages(oldImageNames)).thenReturn(oldBackups);
        when(fileStorageUtils.saveImage(newImages.get(0), newImageNames.get(0))).thenReturn(newSavedFile1);
        when(fileStorageUtils.saveImage(newImages.get(1), newImageNames.get(1))).thenThrow(IOException.class);

        // then
        UncheckedIOException uncheckedIOException = assertThrows(UncheckedIOException.class, () -> imageStorageService.swapImages(oldImageNames, newImages, newImageNames));
        assertEquals("Failed to swap images", uncheckedIOException.getMessage());
        verify(fileStorageUtils).rollbackSavedImages(argumentCaptor.capture());
        assertEquals(newSavedFile1, argumentCaptor.getValue().get(0));
        verify(fileStorageUtils).restoreBackups(oldBackups);
    }

    @Test
    void swapImages_whenValidRequest_swapsImages() throws IOException {
        // given
        List<String> oldImageNames = new ArrayList<>();
        oldImageNames.add("Old image name 1");
        List<MultipartFile> newImages = new ArrayList<>();
        newImages.add(mock(MultipartFile.class));
        List<String> newImageNames = new ArrayList<>();
        newImageNames.add("New image name 1");
        Map<Path, byte[]> oldBackups = new HashMap<>();
        Path newSavedFile1 = Paths.get(tempDir.toString(), newImageNames.get(0));

        // when
        when(fileStorageUtils.backupImages(oldImageNames)).thenReturn(oldBackups);
        when(fileStorageUtils.saveImage(newImages.get(0), newImageNames.get(0))).thenReturn(newSavedFile1);

        // then
        imageStorageService.swapImages(oldImageNames, newImages, newImageNames);
        verify(fileStorageUtils).saveImage(newImages.get(0), newImageNames.get(0));
    }

    @Test
    void deleteImagesFromFolder_whenDeleteFails_throwsUncheckedIOException() throws IOException {
        // given
        List<String> imageNames = new ArrayList<>();
        imageNames.add("Name to delete 1");
        Map<Path, byte[]> backups = new HashMap<>();
        Path emptyPath = Path.of(".");
        backups.put(emptyPath, new byte[1]);

        // when
        when(fileStorageUtils.backupImages(imageNames)).thenReturn(backups);
        doThrow(IOException.class).when(fileStorageUtils).deleteFiles(any(Path.class));

        // then
        UncheckedIOException uncheckedIOException = assertThrows(UncheckedIOException.class, () -> imageStorageService.deleteImagesFromFolder(imageNames));
        assertEquals("Failed to delete all images. Rolled back changes.", uncheckedIOException.getMessage());
    }

    @Test
    void deleteImagesFromFolder_whenValidRequest_deletesImages() throws IOException {
        // given
        List<String> imageNames = new ArrayList<>();
        imageNames.add("Name to delete 1");
        Map<Path, byte[]> backups = new HashMap<>();
        Path emptyPath = Path.of(".");
        backups.put(emptyPath, new byte[1]);

        // when
        when(fileStorageUtils.backupImages(imageNames)).thenReturn(backups);

        // then
        imageStorageService.deleteImagesFromFolder(imageNames);
        verify(fileStorageUtils).deleteFiles(emptyPath);
    }
}
