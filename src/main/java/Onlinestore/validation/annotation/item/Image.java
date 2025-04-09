package Onlinestore.validation.annotation.item;

import Onlinestore.validation.validator.item.IsImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsImageValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Image {
    String message() default "logo is not am image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
