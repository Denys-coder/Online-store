package Onlinestore.service;

import Onlinestore.controller.model.OnlyDigitsConstraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OnlyDigitsValidator implements ConstraintValidator<OnlyDigitsConstraint, String>
{
    @Override
    public void initialize(OnlyDigitsConstraint digits)
    {
    }
    
    @Override
    public boolean isValid(String digits, ConstraintValidatorContext cxt)
    {
        return digits != null && digits.matches("[0-9]+");
    }
}