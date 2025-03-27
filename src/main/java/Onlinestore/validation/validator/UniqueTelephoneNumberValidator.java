package Onlinestore.validation.validator;

import Onlinestore.repository.UserRepository;
import Onlinestore.validation.annotation.UniqueTelephoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueTelephoneNumberValidator implements ConstraintValidator<UniqueTelephoneNumber, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String telephoneNumber, ConstraintValidatorContext context) {
        return telephoneNumber != null && !userRepository.existsByTelephoneNumber(telephoneNumber);
    }
}