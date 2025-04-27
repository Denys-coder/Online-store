package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.validation.annotation.item.MaxFileCount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MaxFileCountValidator implements ConstraintValidator<MaxFileCount, MultipartFile[]> {

    private int maxFilesAmount;

    @Override
    public void initialize(MaxFileCount constraintAnnotation) {
        this.maxFilesAmount = constraintAnnotation.maxFileAmount();
    }

    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        if (files == null || files.length == 0) {
            return true; // No files uploaded, valid case
        }

        if (files.length <= maxFilesAmount) {
            return true;
        }

        // Dynamic error message
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.format("You uploaded %d files, but the maximum allowed is %d", files.length, maxFilesAmount)
        ).addConstraintViolation();

        return false;
    }
}
