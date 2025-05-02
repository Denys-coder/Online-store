package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.image.ImageDTO;
import Onlinestorerestapi.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {

        ImageDTO imageDTO = imageService.getImageDTO(imageName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageDTO.getImage().getFilename() + "\"")
                .body(imageDTO.getImage());

    }

}
