package Onlinestorerestapi.service;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public interface FileOperationsService {
    void deleteIfExists(Path path) throws IOException;

    byte[] readAllBytes(Path path) throws IOException;

    Path write(Path path, byte[] bytes, OpenOption... options) throws IOException;
}
