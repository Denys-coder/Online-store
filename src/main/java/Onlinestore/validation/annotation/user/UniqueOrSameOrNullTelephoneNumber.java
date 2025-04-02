package Onlinestore.validation.annotation.user;

import Onlinestore.validation.validator.user.UniqueOrSameOrNullTelephoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueOrSameOrNullTelephoneNumberValidator.class)
@Documented
public @interface UniqueOrSameOrNullTelephoneNumber {
    String message() default "Telephone number should be unique, null or the same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
