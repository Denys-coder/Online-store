package Onlinestore.validation.validator;

import Onlinestore.repository.UserRepository;
import Onlinestore.validation.annotation.UniqueTelephoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueTelephoneNumberValidator implements ConstraintValidator<UniqueTelephoneNumber, String> {

    private final UserRepository userRepository;

    public UniqueTelephoneNumberValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String telephoneNumber, ConstraintValidatorContext context) {
        return telephoneNumber != null && !userRepository.existsByTelephoneNumber(telephoneNumber);
    }
}