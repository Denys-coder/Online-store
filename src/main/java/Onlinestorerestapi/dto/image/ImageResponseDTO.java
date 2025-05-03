package Onlinestorerestapi.dto.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
public class ImageResponseDTO {
    private Resource image;
    private String contentType;
}
