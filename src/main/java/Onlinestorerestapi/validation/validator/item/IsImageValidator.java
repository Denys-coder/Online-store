package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.service.image.ImageValidationService;
import Onlinestorerestapi.validation.annotation.item.Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class IsImageValidator implements ConstraintValidator<Image, MultipartFile> {

    private final ImageValidationService imageValidationService;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return imageValidationService.isImage(file);
    }
}
