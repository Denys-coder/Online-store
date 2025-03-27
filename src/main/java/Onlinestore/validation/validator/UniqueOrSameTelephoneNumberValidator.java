package Onlinestore.validation.validator;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import Onlinestore.validation.annotation.UniqueOrSameTelephoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UniqueOrSameTelephoneNumberValidator implements ConstraintValidator<UniqueOrSameTelephoneNumber, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String telephoneNumber, ConstraintValidatorContext context) {
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return telephoneNumber != null && (!userRepository.existsByTelephoneNumber(telephoneNumber) || telephoneNumber.equals(currentUser.getTelephoneNumber()));
    }
}
