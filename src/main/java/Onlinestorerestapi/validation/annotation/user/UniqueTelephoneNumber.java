package Onlinestorerestapi.validation.annotation.user;

import Onlinestorerestapi.validation.validator.user.UniqueTelephoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTelephoneNumberValidator.class)
@Documented
public @interface UniqueTelephoneNumber {
    String message() default "Telephone number already in use";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
