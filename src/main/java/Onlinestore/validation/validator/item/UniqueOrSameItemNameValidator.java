package Onlinestore.validation.validator.item;

import Onlinestore.dto.item.PutItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.repository.ItemRepository;
import Onlinestore.validation.annotation.item.UniqueOrSameItemName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UniqueOrSameItemNameValidator implements ConstraintValidator<UniqueOrSameItemName, PutItemDTO> {

    private final ItemRepository itemRepository;

    public UniqueOrSameItemNameValidator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public boolean isValid(PutItemDTO putItemDTO, ConstraintValidatorContext context) {
        Optional<Item> itemOptional = itemRepository.findById(putItemDTO.getId());
        if (itemOptional.isEmpty()) {
            return false;
        }

        Item item = itemOptional.get();
        String putItemDTOName = putItemDTO.getName();
        String currentItemName = item.getName();
        return putItemDTOName != null && (!itemRepository.existsByName(putItemDTOName) || putItemDTOName.equals(currentItemName));
    }
}
