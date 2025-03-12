package Onlinestore.validation.validator;

import Onlinestore.repository.UserRepository;
import Onlinestore.validation.annotation.UniqueTelephone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueTelephoneValidator implements ConstraintValidator<UniqueTelephone, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String telephone, ConstraintValidatorContext context) {
        return telephone != null && !userRepository.existsByTelephoneNumber(telephone);
    }
}