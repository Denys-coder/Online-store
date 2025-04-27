package Onlinestorerestapi.validation.annotation.item;

import Onlinestorerestapi.validation.validator.item.MaxFileCountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MaxFileCountValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxFileCount {
    String message() default ""; // message specified in validator as it needs to be dynamic

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max(); // Maximum allowed files
}
