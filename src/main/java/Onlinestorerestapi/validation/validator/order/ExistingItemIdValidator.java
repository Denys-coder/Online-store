package Onlinestorerestapi.validation.validator.order;

import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.validation.annotation.order.ExistingItemId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistingItemIdValidator implements ConstraintValidator<ExistingItemId, Integer> {

    private final ItemRepository itemRepository;

    public ExistingItemIdValidator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public boolean isValid(Integer itemId, ConstraintValidatorContext context) {

        if (itemId == null) {
            return true;
        }

        return itemRepository.existsById(itemId);
    }
}
