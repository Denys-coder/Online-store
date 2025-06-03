package Onlinestorerestapi.service.image;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ImageServiceConfig {

    @Bean
    public Tika tika() {
        return new Tika();
    }
}
