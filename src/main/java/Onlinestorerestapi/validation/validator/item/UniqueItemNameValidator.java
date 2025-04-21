package Onlinestorerestapi.validation.validator.item;

import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.validation.annotation.item.UniqueItemName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueItemNameValidator implements ConstraintValidator<UniqueItemName, String> {

    private final ItemRepository itemRepository;

    public UniqueItemNameValidator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public boolean isValid(String itemName, ConstraintValidatorContext context) {

        if (itemName == null || itemName.isEmpty()) {
            return true;
        }

        return !itemRepository.existsByName(itemName);
    }
}
