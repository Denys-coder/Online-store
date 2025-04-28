package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.util.ImageValidationUtils;
import Onlinestorerestapi.validation.annotation.item.Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class IsImageValidator implements ConstraintValidator<Image, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return ImageValidationUtils.isImage(file);
    }
}
