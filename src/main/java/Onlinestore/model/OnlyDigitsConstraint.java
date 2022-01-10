package Onlinestore.model;

import Onlinestore.service.OnlyDigitsValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OnlyDigitsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyDigitsConstraint
{
    String message() default "must contain only digits";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
