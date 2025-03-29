package Onlinestore.validation.annotation;

import Onlinestore.validation.validator.UniqueOrSameTelephoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueOrSameTelephoneNumberValidator.class)
@Documented
public @interface UniqueOrSameTelephoneNumber {
    String message() default "Telephone number should be unique or the same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}