package Onlinestorerestapi.dto.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;

import java.util.List;

// 415 Unsupported Media Type
@Getter
@Setter
@NoArgsConstructor
public class UnsupportedMediaTypeDTO {
    private String error = "Unsupported media type";
    private List<MediaType> supportedMediaTypes;

    public UnsupportedMediaTypeDTO(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }
}
