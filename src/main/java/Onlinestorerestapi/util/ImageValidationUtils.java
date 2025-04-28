package Onlinestorerestapi.util;


import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class ImageValidationUtils {

    private static final Tika tika = new Tika();

    public static boolean isImage(MultipartFile file) {
        if (file == null) {
            return true; // Assume null files are handled elsewhere (optional depending on use case)
        }

        try {
            // Check declared Content-Type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return false;
            }

            // Check a real file type using magic bytes
            try (InputStream inputStream = file.getInputStream()) {
                String detectedType = tika.detect(inputStream);
                return detectedType != null && detectedType.startsWith("image/");
            }

        } catch (IOException exception) {
            return false;
        }
    }
}
