package Onlinestore.validation.validator.user;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.validation.annotation.user.UniqueOrSameTelephoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UniqueOrSameTelephoneNumberValidator implements ConstraintValidator<UniqueOrSameTelephoneNumber, String> {

    private final UserRepository userRepository;

    public UniqueOrSameTelephoneNumberValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String telephoneNumber, ConstraintValidatorContext context) {

        if (telephoneNumber == null || telephoneNumber.isEmpty()) {
            return true;
        }

        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return !userRepository.existsByTelephoneNumber(telephoneNumber) || telephoneNumber.equals(currentUser.getTelephoneNumber());
    }
}
