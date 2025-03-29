package Onlinestore.validation.annotation;

import Onlinestore.validation.validator.UniqueOrSameOrNullEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueOrSameOrNullEmailValidator.class)
@Documented
public @interface UniqueOrSameOrNullEmail {
    String message() default "Email address should be unique, null or the same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}