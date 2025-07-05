package Onlinestorerestapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FileStorageService {
    Path saveFiles(MultipartFile file, String fileName) throws IOException;

    Map<Path, byte[]> getFileBytes(List<String> fileNames);

    void saveFiles(Map<Path, byte[]> files);

    void deleteFiles(List<Path> pathsToDelete);
}
