package Onlinestorerestapi.validation.validator.user;

import Onlinestorerestapi.repository.UserRepository;
import Onlinestorerestapi.validation.annotation.user.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (email == null || email.isEmpty()) {
            return true;
        }

        return !userRepository.existsByEmail(email);
    }
}
