package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.service.image.ImageValidationService;
import Onlinestorerestapi.validation.annotation.item.ImageArray;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IsImageArrayValidator implements ConstraintValidator<ImageArray, List<MultipartFile>> {

    private final ImageValidationService imageValidationService;

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null) {
            return true;
        }

        for (MultipartFile file : files) {
            if (!imageValidationService.isImage(file)) {
                return false;
            }
        }

        return true;
    }
}
