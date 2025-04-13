package Onlinestore.validation.validator.user;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.validation.annotation.user.UniqueOrSameEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UniqueOrSameEmailValidator implements ConstraintValidator<UniqueOrSameEmail, String> {

    private final UserRepository userRepository;

    public UniqueOrSameEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (email == null || email.isEmpty()) {
            return true;
        }

        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return !userRepository.existsByEmail(email) || email.equals(currentUser.getEmail());
    }
}
