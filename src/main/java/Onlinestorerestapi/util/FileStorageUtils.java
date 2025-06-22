package Onlinestorerestapi.util;

import Onlinestorerestapi.service.FileOperationsService;
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
public class FileStorageUtils {

    @Value("${images.directory}")
    private String imagesDirectory;

    private final FileOperationsService fileOperationsService;

    public Path saveImage(MultipartFile image, String imageName) throws IOException {
        Path path = Path.of(imagesDirectory).resolve(imageName).normalize();
        Files.write(path, image.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return path;
    }

    public Map<Path, byte[]> backupImages(List<String> imageNames) {
        Map<Path, byte[]> backups = new HashMap<>();
        for (String imageName : imageNames) {
            Path path = Paths.get(imagesDirectory).resolve(imageName).normalize();
            if (Files.exists(path)) {
                try {
                    backups.put(path, fileOperationsService.readAllBytes(path));
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to backup image: " + imageName, e);
                }
            }
        }
        return backups;
    }

    public void restoreBackups(Map<Path, byte[]> backups) {
        for (Map.Entry<Path, byte[]> entry : backups.entrySet()) {
            try {
                fileOperationsService.write(entry.getKey(), entry.getValue(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                System.out.printf("Failed to restore backup: %s - %s%n", entry.getKey(), ex.getMessage());
            }
        }
    }

    public void rollbackSavedImages(List<Path> savedFiles) {
        for (Path path : savedFiles) {
            try {
                fileOperationsService.deleteIfExists(path);
            } catch (IOException ex) {
                System.out.printf("Rollback failed for newly saved file: %s - %s%n", path, ex.getMessage());
            }
        }
    }
}
