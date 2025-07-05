package Onlinestorerestapi.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@Service
public class FileOperationsServiceImpl implements FileOperationsService {

    public void deleteIfExists(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public Path write(Path path, byte[] bytes, OpenOption... options) throws IOException {
        return Files.write(path, bytes, options);
    }
}
