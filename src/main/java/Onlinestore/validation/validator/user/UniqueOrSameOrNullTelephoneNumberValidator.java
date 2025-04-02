package Onlinestore.validation.validator.user;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.validation.annotation.user.UniqueOrSameOrNullTelephoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UniqueOrSameOrNullTelephoneNumberValidator implements ConstraintValidator<UniqueOrSameOrNullTelephoneNumber, String> {

    private final UserRepository userRepository;

    public UniqueOrSameOrNullTelephoneNumberValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String telephoneNumber, ConstraintValidatorContext context) {
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (telephoneNumber == null) {
            return true;
        }

        return (!userRepository.existsByTelephoneNumber(telephoneNumber)) || telephoneNumber.equals(currentUser.getTelephoneNumber());
    }
}
