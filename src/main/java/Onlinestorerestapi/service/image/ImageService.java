package Onlinestorerestapi.service.image;

import Onlinestorerestapi.dto.image.ImageResponseDTO;

public interface ImageService {
    ImageResponseDTO getImageDTO(String imageName);

    String getImagesDirectory();

    void setImagesDirectory(String imagesDirectory);
}
