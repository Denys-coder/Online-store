package Onlinestore.validation.validator.user;

import Onlinestore.repository.UserRepository;
import Onlinestore.validation.annotation.user.UniqueTelephoneNumber;
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

        if (telephoneNumber == null || telephoneNumber.isEmpty()) {
            return true;
        }

        return !userRepository.existsByTelephoneNumber(telephoneNumber);
    }
}
