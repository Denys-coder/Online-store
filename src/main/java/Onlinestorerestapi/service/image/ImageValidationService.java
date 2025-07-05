package Onlinestorerestapi.service.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageValidationService {
    boolean isImage(MultipartFile file);
}
