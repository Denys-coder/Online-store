package Onlinestore.validation.annotation;

import Onlinestore.validation.validator.UniqueOrSameEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueOrSameEmailValidator.class)
@Documented
public @interface UniqueOrSameEmail {
    String message() default "Email address already in use";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}