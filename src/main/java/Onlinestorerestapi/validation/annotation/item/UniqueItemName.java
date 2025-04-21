package Onlinestorerestapi.validation.annotation.item;

import Onlinestorerestapi.validation.validator.item.UniqueItemNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueItemNameValidator.class)
@Documented
public @interface UniqueItemName {
    String message() default "Item name should be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
