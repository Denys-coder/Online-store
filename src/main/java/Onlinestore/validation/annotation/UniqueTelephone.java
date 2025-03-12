package Onlinestore.validation.annotation;

import Onlinestore.validation.validator.UniqueTelephoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTelephoneValidator.class)
@Documented
public @interface UniqueTelephone {
    String message() default "Telephone number already in use";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
