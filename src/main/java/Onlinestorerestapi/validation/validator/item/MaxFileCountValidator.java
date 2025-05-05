package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.validation.annotation.item.MaxFileCount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MaxFileCountValidator implements ConstraintValidator<MaxFileCount, List<MultipartFile>> {

    private int maxFilesAmount;

    @Override
    public void initialize(MaxFileCount constraintAnnotation) {
        this.maxFilesAmount = constraintAnnotation.maxFileAmount();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true; // No files uploaded, valid case
        }

        if (files.size() <= maxFilesAmount) {
            return true;
        }

        // Dynamic error message
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.format("You uploaded %d files, but the maximum allowed is %d", files.size(), maxFilesAmount)
        ).addConstraintViolation();

        return false;
    }
}
