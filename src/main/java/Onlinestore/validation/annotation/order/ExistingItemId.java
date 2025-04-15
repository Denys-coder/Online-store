package Onlinestore.validation.annotation.order;

import Onlinestore.validation.validator.order.ExistingItemIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingItemIdValidator.class)
@Documented
public @interface ExistingItemId {
    String message() default "There is no item with the specified id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
