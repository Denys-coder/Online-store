package Onlinestore.validation.validator.item;

import Onlinestore.validation.annotation.item.MaxFileCount;
import Onlinestore.validation.exception.item.MaxFileCountExceededException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MaxFileCountValidator implements ConstraintValidator<MaxFileCount, MultipartFile[]> {

    private int maxFiles;

    @Override
    public void initialize(MaxFileCount constraintAnnotation) {
        this.maxFiles = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        if (files == null) {
            return true; // No files uploaded, valid case
        }

        if (files.length > maxFiles) {
            throw new MaxFileCountExceededException("Max upload file number limit is " + maxFiles);
        }

        return true;
    }
}
