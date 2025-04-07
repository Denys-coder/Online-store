package Onlinestore.validation.validator.item;

import Onlinestore.repository.ItemRepository;
import Onlinestore.validation.annotation.item.UniqueItemName;
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
        return itemName != null && !itemRepository.existsByName(itemName);
    }
}
