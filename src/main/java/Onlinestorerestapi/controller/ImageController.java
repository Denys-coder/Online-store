package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.image.ImageResponseDTO;
import Onlinestorerestapi.service.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "image", description = "Used to work with image")
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Get an image by its name, which is its id")
    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {

        ImageResponseDTO imageResponseDTO = imageService.getImageDTO(imageName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageResponseDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResponseDTO.getImage().getFilename() + "\"")
                .body(imageResponseDTO.getImage());

    }
}
