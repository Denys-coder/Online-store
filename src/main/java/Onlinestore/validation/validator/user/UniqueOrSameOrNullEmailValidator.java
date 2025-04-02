package Onlinestore.validation.validator.user;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.validation.annotation.user.UniqueOrSameOrNullEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UniqueOrSameOrNullEmailValidator implements ConstraintValidator<UniqueOrSameOrNullEmail, String> {

    private final UserRepository userRepository;

    public UniqueOrSameOrNullEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (email == null) {
            return true;
        }

        return (!userRepository.existsByEmail(email)) || email.equals(currentUser.getEmail());
    }
}
