package Onlinestore.validation.annotation;


import Onlinestore.validation.validator.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "Email address already in use";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
