package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.util.ImageValidationUtils;
import Onlinestorerestapi.validation.annotation.item.ImageArray;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class IsImageArrayValidator implements ConstraintValidator<ImageArray, MultipartFile[]> {
    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        if (files == null) {
            return true;
        }

        for (MultipartFile file : files) {
            if (!ImageValidationUtils.isImage(file)) {
                return false;
            }
        }

        return true;
    }
}
