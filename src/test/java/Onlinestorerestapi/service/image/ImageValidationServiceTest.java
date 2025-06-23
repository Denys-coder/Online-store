package Onlinestorerestapi.service.image;

import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageValidationServiceTest {

    @Mock
    private Tika tika;

    @InjectMocks
    private ImageValidationService imageValidationService;

    @Test
    void isImage_nullFile_returnsTrue() {
        assertTrue(imageValidationService.isImage(null));
    }

    @Test
    void isImage_invalidContentType_returnsFalse() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");

        assertFalse(imageValidationService.isImage(file));
        verify(file, never()).getInputStream(); // ensure Tika was never used
    }

    @Test
    void isImage_validDeclaredAndActualImage_returnsTrue() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        when(tika.detect(any(InputStream.class))).thenReturn("image/png");

        assertTrue(imageValidationService.isImage(file));
    }

    @Test
    void isImage_validDeclaredButInvalidActualImage_returnsFalse() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        when(tika.detect(any(InputStream.class))).thenReturn("application/pdf");

        assertFalse(imageValidationService.isImage(file));
    }

    @Test
    void isImage_getInputStreamThrowsIOException_returnsFalse() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getInputStream()).thenThrow(new IOException("read error"));

        assertFalse(imageValidationService.isImage(file));
    }

    @Test
    void isImage_nullContentType_returnsFalse() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(null);

        assertFalse(imageValidationService.isImage(file));
    }
}