package Onlinestorerestapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${images.directory}")
    private String imagesDirectory;

    private final FileOperationsService fileOperationsService;

    public Path saveFiles(MultipartFile file, String fileName) throws IOException {
        Path path = Path.of(imagesDirectory).resolve(fileName).normalize();
        Files.write(path, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return path;
    }

    public Map<Path, byte[]> getFileBytes(List<String> fileNames) {
        Map<Path, byte[]> fileBytes = new HashMap<>();
        for (String fileName : fileNames) {
            Path path = Paths.get(imagesDirectory).resolve(fileName).normalize();
            if (Files.exists(path)) {
                try {
                    fileBytes.put(path, fileOperationsService.readAllBytes(path));
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to get bytes of file: " + fileName, e);
                }
            }
        }
        return fileBytes;
    }

    public void saveFiles(Map<Path, byte[]> files) {
        for (Map.Entry<Path, byte[]> entry : files.entrySet()) {
            try {
                fileOperationsService.write(entry.getKey(), entry.getValue(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                System.out.printf("Failed to save files: %s - %s%n", entry.getKey(), ex.getMessage());
            }
        }
    }

    public void deleteFiles(List<Path> pathsToDelete) {
        for (Path path : pathsToDelete) {
            try {
                fileOperationsService.deleteIfExists(path);
            } catch (IOException ex) {
                System.out.printf("Failed to delete: %s - %s%n", path, ex.getMessage());
            }
        }
    }
}
