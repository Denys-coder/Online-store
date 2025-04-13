package Onlinestore.validation.validator.item;

import Onlinestore.validation.annotation.item.Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class IsImageValidator implements ConstraintValidator<Image, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null) {
            return true;
        }

        try {

            // First layer: check declared Content-Type from the request
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return false;
            }

            // Second layer: verify actual content (magic bytes)
            InputStream inputStream = file.getInputStream();
            String detectedType = new Tika().detect(inputStream);
            if (detectedType == null || !detectedType.startsWith("image/")) {
                return false;
            }

            return true;

        } catch (IOException e) {
            return false;
        }

    }
}
