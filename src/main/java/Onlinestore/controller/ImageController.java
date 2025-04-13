package Onlinestore.controller;

import lombok.AllArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
public class ImageController {

    private final Environment environment;


    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path imagesDir = Paths.get(environment.getProperty("images.directory")).normalize();
            Path imagePath = imagesDir.resolve(imageName).normalize();

            // Prevent path traversal
            if (!imagePath.startsWith(imagesDir)) {
                return ResponseEntity.badRequest().body(null);
            }

            // Load resource
            Resource resource = new UrlResource(imagePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Detect MIME type using Tika
            Tika tika = new Tika();
            String contentType = tika.detect(imagePath.toFile());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
