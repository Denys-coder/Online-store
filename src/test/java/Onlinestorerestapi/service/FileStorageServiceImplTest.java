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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceImplTest {

    @TempDir
    Path tempPath;

    @InjectMocks
    FileStorageServiceImpl fileStorageService;

    @Mock
    FileOperationsService fileOperationsService;

    @BeforeEach
    public void setUpImagesDirectory() {
        ReflectionTestUtils.setField(fileStorageService, "imagesDirectory", tempPath.toString());
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
        assertTrue(Files.exists(tempPath.resolve(imageName).normalize()));
    }

    @Test
    public void getFileBytes_whenPathExistsButCouldNotReadBytes_throwsUncheckedIOException() throws IOException {
        // given
        String fileName = "file name 1";
        List<String> fileNames = List.of(fileName);
        byte[] fileContent = "file-content".getBytes();
        Path path = Paths.get(tempPath.toString()).resolve(fileName).normalize();
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
        Path path = Paths.get(tempPath.toString()).resolve(fileName).normalize();
        Files.write(path, fileContent);

        // when
        when(fileOperationsService.readAllBytes(path)).thenReturn(fileContent);

        // then
        Map<Path, byte[]> fileBytes = fileStorageService.getFileBytes(fileNames);
        assertEquals(fileBytes.get(path), fileBytes.get(path));
    }

    @Test
    public void saveFiles_whenWriteFails_printsError() throws IOException {
        // given
        byte[] fileContent = new byte[10];
        Path filePath = tempPath.resolve("file name 1").normalize();
        Map<Path, byte[]> files = new HashMap<>();
        files.put(filePath, fileContent);
        String exceptionMessage = String.format("Failed to save file: %s", filePath.toString());

        // when
        doThrow(new IOException()).when(fileOperationsService).write(
                eq(filePath), eq(fileContent), eq(StandardOpenOption.CREATE), eq(StandardOpenOption.TRUNCATE_EXISTING)
        );

        // then
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        try {
            fileStorageService.saveFiles(files);
            String output = errContent.toString().trim();
            assertEquals(exceptionMessage, output);
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void saveFiles_successfullySavesFiles() throws IOException {
        // given
        byte[] fileContent = new byte[10];
        Path filePath = tempPath.resolve("file name 1").normalize();
        Map<Path, byte[]> files = new HashMap<>();
        files.put(filePath, fileContent);

        // then
        fileStorageService.saveFiles(files);
        verify(fileOperationsService).write(filePath, fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Test
    public void deleteFiles_whenDeleteFails_printsError() throws IOException {
        // given
        List<Path> pathsToDelete = new ArrayList<>();
        Path filePath = tempPath.resolve("file name 1").normalize();
        pathsToDelete.add(filePath);
        String exceptionMessage = String.format("Failed to delete file: %s", filePath.toString());

        // when
        doThrow(new IOException()).when(fileOperationsService).deleteIfExists(filePath);

        // then
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        try {
            fileStorageService.deleteFiles(pathsToDelete);
            String output = errContent.toString().trim();
            assertEquals(exceptionMessage, output);
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    public void deleteFiles_successfullyDeleteFiles() throws IOException {
        // given
        List<Path> pathsToDelete = new ArrayList<>();
        Path filePath = tempPath.resolve("file name 1").normalize();
        pathsToDelete.add(filePath);

        // then
        fileStorageService.deleteFiles(pathsToDelete);
        verify(fileOperationsService).deleteIfExists(filePath);
    }
}
