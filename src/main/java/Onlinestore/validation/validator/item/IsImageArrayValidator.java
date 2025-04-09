package Onlinestore.validation.validator.item;

import Onlinestore.validation.annotation.item.ImageArray;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class IsImageArrayValidator implements ConstraintValidator<ImageArray, MultipartFile[]> {
    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        try {

            for (MultipartFile file : files) {

                // First layer: check declared Content-Type from the request
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return false;
                }

                // Second layer: verify actual content (magic bytes)
                String detectedType = new Tika().detect(file.getInputStream());
                if (detectedType == null || !detectedType.startsWith("image/")) {
                    return false;
                }
            }

            // all passed
            return true;

        } catch (IOException e) {
            return false;
        }
    }
}
