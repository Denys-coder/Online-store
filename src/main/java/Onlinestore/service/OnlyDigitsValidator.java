package Onlinestore.service;

import Onlinestore.model.OnlyDigitsConstraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OnlyDigitsValidator implements ConstraintValidator<OnlyDigitsConstraint, String>
{
    @Override
    public void initialize(OnlyDigitsConstraint telephoneNumberNumber)
    {
    }
    
    @Override
    public boolean isValid(String telephoneNumber, ConstraintValidatorContext cxt)
    {
        return telephoneNumber != null && telephoneNumber.matches("[0-9]+");
    }
}