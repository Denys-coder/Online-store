package Onlinestore.validation.annotation.item;

import Onlinestore.validation.validator.item.UniqueItemNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueItemNameValidator.class)
@Documented
public @interface UniqueItemName {
    String message() default "There is already an item with this name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
