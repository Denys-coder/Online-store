package Onlinestorerestapi.service.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    Environment environment;

    @Test
    void constructor_whenImageDirectoryIsNull() {

        // when
        when(environment.getProperty("images.directory")).thenReturn(null);

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new ImageService(environment));
        assertEquals("Property 'images.directory' is not set.", illegalArgumentException.getMessage());
    }

    @Test
    void constructor_whenImageDirectoryIsBlank() {

        // when
        when(environment.getProperty("images.directory")).thenReturn("");

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new ImageService(environment));
        assertEquals("Property 'images.directory' is not set.", illegalArgumentException.getMessage());
    }
}
