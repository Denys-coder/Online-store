package Onlinestorerestapi.validation.annotation.item;

import Onlinestorerestapi.validation.validator.item.IsImageArrayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsImageArrayValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageArray {
    String message() default "At least one of the images is not an image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
