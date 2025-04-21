package Onlinestorerestapi.validation.annotation.item;

import Onlinestorerestapi.validation.validator.item.UniqueOrSameItemNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueOrSameItemNameValidator.class)
@Documented
public @interface UniqueOrSameItemName {
    String message() default "Item name should be unique or same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
